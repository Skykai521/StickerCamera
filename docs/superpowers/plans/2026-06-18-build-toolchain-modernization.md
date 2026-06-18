# StickerCamera 编译链现代化 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把 StickerCamera 的编译链升级到 AGP 8.11 / Gradle 8.13 / JDK 17 / AndroidX,在现代开发机上构建通过并保持原有功能。

**Architecture:** 先升级构建系统骨架(wrapper/根/settings/properties),再逐模块(两个 library 可独立验证),最后迁移 :app 源码(AndroidX、ButterKnife、EventBus、死库替换、edge-to-edge),以 `assembleDebug` + 模拟器冒烟为终点验证。

**Tech Stack:** Android Gradle Plugin 8.11.x, Gradle 8.13, JDK 17, AndroidX(appcompat/recyclerview/cardview/core/fragment/viewpager/swiperefreshlayout), Material Components, ButterKnife 10.2.3, EventBus 3.3.1, GPUImage(prebuilt .so)。

## Global Constraints

- JDK 运行版本:17;Java source/target:17(移除 Retrolambda)。
- Gradle:8.13;AGP:8.11.1(以"能稳定支持 compileSdk 36 的最近 AGP 8.11+ / 配套 Gradle"为准)。
- compileSdk = 36;targetSdk = 36;minSdk = 26。
- 仓库:仅 `google()` + `mavenCentral()`(+ pluginManagement 加 `gradlePluginPortal()`);禁止 `jcenter()`。
- AndroidX:`android.useAndroidX=true`;为降低旧工程风险 `android.nonTransitiveRClass=false`(保留传递 R,避免跨模块 R 引用断裂)。
- 各模块必须声明 `namespace`,并从对应 `AndroidManifest.xml` 删除 `package=`。
- 依赖配置用 `implementation`/`api`,不用已废弃的 `compile`。
- 死库一律移除:`systembartint`、melnykov `floatingactionbutton`、rengwuxian `materialedittext`、sephiroth `hlistview`。
- 模块 namespace:`:app`=`com.github.skykai.stickercamera`,`:Gpu-Image`=`jp.co.cyberagent.android.gpuimage`,`:ImageViewTouch`=`com.imagezoom`。

> 说明:本工程无单元测试,"测试周期"为构建/grep 验证。:app 源码在全部迁移完成前无法编译,因此 Task 5–10 的验证用定向 grep/检视,首个完整构建绿灯在 Task 11。

---

### Task 1: 构建系统骨架

**Files:**
- Modify: `gradle/wrapper/gradle-wrapper.properties`
- Modify: `build.gradle`(根)
- Modify: `settings.gradle`
- Modify: `gradle.properties`
- Create: `local.properties`(不入 git)

- [ ] **Step 1: 升级 Gradle wrapper**

`gradle/wrapper/gradle-wrapper.properties` 的 `distributionUrl` 改为:

```
distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-all.zip
```

- [ ] **Step 2: 重写根 `build.gradle`**

```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.11.1'
    }
}
```

(删除 retrolambda classpath、`allprojects {}` 与所有 `jcenter()`。)

- [ ] **Step 3: 重写 `settings.gradle`**

```gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "StickerCamera"
include ':app', ':Gpu-Image', ':ImageViewTouch'
```

- [ ] **Step 4: 更新 `gradle.properties`**

追加:

```
android.useAndroidX=true
android.nonTransitiveRClass=false
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

- [ ] **Step 5: 写 `local.properties`**

```
sdk.dir=/Users/skykai/Library/Android/sdk
```

- [ ] **Step 6: 验证 Gradle/AGP 起得来**

Run: `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew --version`
Expected: `Gradle 8.13`

Run: `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew projects`
Expected: 列出 `:app`、`:Gpu-Image`、`:ImageViewTouch`,无 jcenter/AGP 报错(模块自身配置报错下一步处理)。

- [ ] **Step 7: Commit**

```bash
git add gradle/wrapper/gradle-wrapper.properties build.gradle settings.gradle gradle.properties
git commit -m "build: 升级 Gradle 8.13 + AGP 8.11 骨架,仓库切换到 google/mavenCentral"
```

---

### Task 2: ImageViewTouch library 模块

**Files:**
- Modify: `ImageViewTouch/build.gradle`
- Modify: `ImageViewTouch/AndroidManifest.xml`

**Interfaces:**
- Produces: AndroidX 版 `com.imagezoom.ImageViewTouch`(供 :app 使用,API 不变)。

- [ ] **Step 1: 重写 `ImageViewTouch/build.gradle`**

```gradle
apply plugin: 'com.android.library'

android {
    namespace 'com.imagezoom'
    compileSdk 36

    defaultConfig {
        minSdk 26
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    lint {
        abortOnError false
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.7.0'
}
```

- [ ] **Step 2: 精简 `ImageViewTouch/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
```

(删除 `package=` 与 `<application>` 块,namespace 已提供包名,避免 library label 合并问题。)

- [ ] **Step 3: 验证模块独立编译**

Run: `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :ImageViewTouch:assembleDebug`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add ImageViewTouch/build.gradle ImageViewTouch/AndroidManifest.xml
git commit -m "build(ImageViewTouch): AGP 8 + AndroidX + namespace"
```

---

### Task 3: Gpu-Image library 模块(含 .so 迁移)

**Files:**
- Modify: `Gpu-Image/build.gradle`
- Modify: `Gpu-Image/AndroidManifest.xml`
- Delete: `Gpu-Image/libs/armeabi/`、`Gpu-Image/libs/mips/`、`Gpu-Image/libs/mips64/`

**Interfaces:**
- Produces: AndroidX 版 `jp.co.cyberagent.android.gpuimage.*`(GPUImageView/GPUImageFilter 等,API 不变);`.so` 通过 `jniLibs.srcDirs=['libs']` 打包。

- [ ] **Step 1: 删除废弃 ABI**

```bash
git rm -r Gpu-Image/libs/armeabi Gpu-Image/libs/mips Gpu-Image/libs/mips64
```

保留:`arm64-v8a`、`armeabi-v7a`、`x86`、`x86_64`。

- [ ] **Step 2: 重写 `Gpu-Image/build.gradle`**

```gradle
apply plugin: 'com.android.library'

android {
    namespace 'jp.co.cyberagent.android.gpuimage'
    compileSdk 36

    defaultConfig {
        minSdk 26
    }

    sourceSets {
        main {
            manifest.srcFile 'AndroidManifest.xml'
            java.srcDirs = ['src']
            res.srcDirs = ['res']
            assets.srcDirs = ['assets']
            jniLibs.srcDirs = ['libs']
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    lint {
        abortOnError false
    }
}
```

(沿用非标准 `src/`/`res/` 布局;把原 `jni.srcDirs`/`renderscript`/`aidl` 去掉——本模块只有预编译 `.so`,无 native/rs/aidl 源;`.so` 用 `jniLibs.srcDirs=['libs']` 打包。)

- [ ] **Step 3: 清理 `Gpu-Image/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
```

(删除 `package=`。)

- [ ] **Step 4: 验证模块独立编译且打包 .so**

Run: `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :Gpu-Image:assembleDebug`
Expected: `BUILD SUCCESSFUL`

Run: `unzip -l Gpu-Image/build/outputs/aar/Gpu-Image-debug.aar | grep '\.so'`
Expected: 仅列出 `arm64-v8a`、`armeabi-v7a`、`x86`、`x86_64` 下的 `libgpuimage-library.so`

- [ ] **Step 5: Commit**

```bash
git add Gpu-Image/build.gradle Gpu-Image/AndroidManifest.xml Gpu-Image/libs
git commit -m "build(Gpu-Image): AGP 8 + namespace, .so 迁 jniLibs 并裁废弃 ABI"
```

---

### Task 4: :app 构建脚本与依赖映射

**Files:**
- Modify: `app/build.gradle`
- Modify: `app/src/main/AndroidManifest.xml`
- Delete: `app/libs/fastjson-1.2.5.jar`、`app/libs/universal-image-loader-1.9.4.jar`

> 本任务后 :app 仍无法编译(源码未迁移),验证仅到"配置可解析"。

- [ ] **Step 1: 删除被坐标替换的本地 jar**

```bash
git rm app/libs/fastjson-1.2.5.jar app/libs/universal-image-loader-1.9.4.jar
```

- [ ] **Step 2: 重写 `app/build.gradle`**

```gradle
apply plugin: 'com.android.application'

android {
    namespace 'com.github.skykai.stickercamera'
    compileSdk 36

    defaultConfig {
        applicationId "com.github.skykai.stickercamera"
        minSdk 26
        targetSdk 36
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.core:core:1.13.1'
    implementation 'androidx.fragment:fragment:1.8.5'
    implementation 'androidx.viewpager:viewpager:1.0.0'
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'com.jakewharton:butterknife:10.2.3'
    annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
    implementation 'com.alibaba:fastjson:1.2.83'
    implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
    implementation 'org.greenrobot:eventbus:3.3.1'
    implementation project(':Gpu-Image')
    implementation project(':ImageViewTouch')
}
```

- [ ] **Step 3: 删除 manifest 的 `package`**

`app/src/main/AndroidManifest.xml` 的 `<manifest ...>` 删去 `package="com.github.skykai.stickercamera"`,其余(权限、application、activity 全限定名)不变。

- [ ] **Step 4: 验证配置解析**

Run: `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :app:dependencies --configuration debugRuntimeClasspath`
Expected: 依赖树解析成功(出现 androidx/material/eventbus/fastjson),无"Could not resolve"。

- [ ] **Step 5: Commit**

```bash
git add app/build.gradle app/src/main/AndroidManifest.xml app/libs
git commit -m "build(app): AGP 8 依赖映射到 AndroidX/Material,移除死库与本地 jar"
```

---

### Task 5: :app AndroidX 源码迁移

**Files(java,8 个):**
- Modify: `app/src/main/java/com/stickercamera/base/BaseActivity.java:8`
- Modify: `app/src/main/java/com/stickercamera/base/BaseFragmentActivity.java:3`
- Modify: `app/src/main/java/com/stickercamera/app/ui/MainActivity.java:4-9`
- Modify: `app/src/main/java/com/stickercamera/app/camera/ui/AlbumActivity.java:5-8`
- Modify: `app/src/main/java/com/stickercamera/app/camera/fragment/AlbumFragment.java:4`
- Modify: `app/src/main/java/com/stickercamera/app/camera/ui/PhotoProcessActivity.java:12`
- Modify: `app/src/main/java/com/customview/PagerSlidingTabStrip.java:29-30`

**Files(xml):** 含 `android.support.*` 标签的布局(用 grep 定位,通常 `activity_main.xml`、`activity_album.xml`、`item_picture.xml`)。

- [ ] **Step 1: 替换 java import(按下表逐项全局替换)**

| 旧 | 新 |
|---|---|
| `android.support.v7.app.AppCompatActivity` | `androidx.appcompat.app.AppCompatActivity` |
| `android.support.v4.app.Fragment` | `androidx.fragment.app.Fragment` |
| `android.support.v4.app.FragmentActivity` | `androidx.fragment.app.FragmentActivity` |
| `android.support.v4.app.FragmentManager` | `androidx.fragment.app.FragmentManager` |
| `android.support.v4.app.FragmentPagerAdapter` | `androidx.fragment.app.FragmentPagerAdapter` |
| `android.support.v4.view.ViewPager` | `androidx.viewpager.widget.ViewPager` |
| `android.support.v4.view.ViewPager.OnPageChangeListener` | `androidx.viewpager.widget.ViewPager.OnPageChangeListener` |
| `android.support.v4.widget.SwipeRefreshLayout` | `androidx.swiperefreshlayout.widget.SwipeRefreshLayout` |
| `android.support.v7.widget.RecyclerView` | `androidx.recyclerview.widget.RecyclerView` |
| `android.support.v7.widget.LinearLayoutManager` | `androidx.recyclerview.widget.LinearLayoutManager` |
| `android.support.v7.widget.CardView` | `androidx.cardview.widget.CardView` |
| `android.support.annotation.Nullable` | `androidx.annotation.Nullable` |

可用:

```bash
cd app/src/main/java
grep -rl "android.support" . | while read f; do
  sed -i '' \
    -e 's#android\.support\.v7\.app\.AppCompatActivity#androidx.appcompat.app.AppCompatActivity#g' \
    -e 's#android\.support\.v4\.app\.FragmentPagerAdapter#androidx.fragment.app.FragmentPagerAdapter#g' \
    -e 's#android\.support\.v4\.app\.FragmentActivity#androidx.fragment.app.FragmentActivity#g' \
    -e 's#android\.support\.v4\.app\.FragmentManager#androidx.fragment.app.FragmentManager#g' \
    -e 's#android\.support\.v4\.app\.Fragment#androidx.fragment.app.Fragment#g' \
    -e 's#android\.support\.v4\.view\.ViewPager#androidx.viewpager.widget.ViewPager#g' \
    -e 's#android\.support\.v4\.widget\.SwipeRefreshLayout#androidx.swiperefreshlayout.widget.SwipeRefreshLayout#g' \
    -e 's#android\.support\.v7\.widget\.RecyclerView#androidx.recyclerview.widget.RecyclerView#g' \
    -e 's#android\.support\.v7\.widget\.LinearLayoutManager#androidx.recyclerview.widget.LinearLayoutManager#g' \
    -e 's#android\.support\.v7\.widget\.CardView#androidx.cardview.widget.CardView#g' \
    -e 's#android\.support\.annotation\.Nullable#androidx.annotation.Nullable#g' \
    "$f"
done
```

- [ ] **Step 2: 替换 xml 中的全限定 support 标签**

```bash
cd app/src/main/res
grep -rl "android.support" . | while read f; do
  sed -i '' \
    -e 's#android\.support\.v4\.view\.ViewPager#androidx.viewpager.widget.ViewPager#g' \
    -e 's#android\.support\.v7\.widget\.RecyclerView#androidx.recyclerview.widget.RecyclerView#g' \
    -e 's#android\.support\.v7\.widget\.CardView#androidx.cardview.widget.CardView#g' \
    "$f"
done
```

- [ ] **Step 3: 验证无残留**

Run: `grep -rn "android.support" app/src && echo "STILL HAS SUPPORT" || echo "CLEAN"`
Expected: `CLEAN`

- [ ] **Step 4: Commit**

```bash
git add app/src
git commit -m "refactor(app): android.support.* 全量迁移到 androidx"
```

---

### Task 6: :app ButterKnife 6 → 10

**Files(6 个含注解):** `EditTextActivity.java`、`MainActivity.java`、`AlbumActivity.java`、`CameraActivity.java`、`CropPhotoActivity.java`、`PhotoProcessActivity.java`(`BaseActivity.java` 仅有一行无用 `import butterknife.ButterKnife` 可一并删)。

- [ ] **Step 1: 注解与调用改名**

```bash
cd app/src/main/java
grep -rl "InjectView\|ButterKnife" . | while read f; do
  sed -i '' \
    -e 's#import butterknife\.InjectView;#import butterknife.BindView;#g' \
    -e 's#@InjectView#@BindView#g' \
    -e 's#ButterKnife\.inject(#ButterKnife.bind(#g' \
    "$f"
done
```

(若某文件同时有 `@OnClick` 等其它 ButterKnife 注解,10.x API 兼容,无需改;仅 `@InjectView`→`@BindView`、`inject`→`bind` 有变。)

- [ ] **Step 2: 删除 BaseActivity 无用 import**

`app/src/main/java/com/stickercamera/base/BaseActivity.java` 删除 `import butterknife.ButterKnife;`(该类不调用 ButterKnife)。

- [ ] **Step 3: 验证无残留旧 API**

Run: `grep -rn "InjectView\|ButterKnife.inject" app/src && echo "STILL OLD" || echo "CLEAN"`
Expected: `CLEAN`

- [ ] **Step 4: Commit**

```bash
git add app/src
git commit -m "refactor(app): ButterKnife 6 -> 10 (@BindView/bind)"
```

---

### Task 7: :app EventBus 2 → 3

**Files:** `MainActivity.java`、`PhotoProcessActivity.java`

- [ ] **Step 1: 改包名 import**

两文件:`import de.greenrobot.event.EventBus;` → `import org.greenrobot.eventbus.EventBus;`
`MainActivity.java` 另加:`import org.greenrobot.eventbus.Subscribe;` 和 `import org.greenrobot.eventbus.ThreadMode;`

- [ ] **Step 2: 注解订阅方法(MainActivity)**

`MainActivity.java` 中:

```java
public void onEventMainThread(FeedItem feedItem) {
```

改为:

```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onEvent(FeedItem feedItem) {
```

(`register`/`unregister`/`post` API 在 3.x 不变,无需改。)

- [ ] **Step 3: 验证无残留旧包**

Run: `grep -rn "de.greenrobot" app/src && echo "STILL OLD" || echo "CLEAN"`
Expected: `CLEAN`

- [ ] **Step 4: Commit**

```bash
git add app/src
git commit -m "refactor(app): EventBus 2.4 -> 3.3.1 (@Subscribe)"
```

---

### Task 8: melnykov FAB → Material FAB

**Files:** `app/src/main/res/layout/activity_main.xml`、`app/src/main/java/com/stickercamera/app/ui/MainActivity.java`

- [ ] **Step 1: 替换布局中的 FAB**

`activity_main.xml` 把 `com.melnykov.fab.FloatingActionButton` 节点整体替换为:

```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fab"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_alignParentBottom="true"
    android:layout_centerHorizontal="true"
    android:layout_marginBottom="31dp"
    android:src="@drawable/ic_camera_white"
    app:backgroundTint="?attr/colorPrimary"
    app:rippleColor="?attr/colorPrimaryDark"
    app:tint="@android:color/white" />
```

- [ ] **Step 2: 替换 MainActivity import**

`MainActivity.java`:`import com.melnykov.fab.FloatingActionButton;` → `import com.google.android.material.floatingactionbutton.FloatingActionButton;`
(字段 `FloatingActionButton fab;` 与 `fab.setOnClickListener(...)` 不变。)

- [ ] **Step 3: 验证无残留**

Run: `grep -rn "melnykov" app/src && echo "STILL OLD" || echo "CLEAN"`
Expected: `CLEAN`

- [ ] **Step 4: Commit**

```bash
git add app/src
git commit -m "refactor(app): melnykov FAB -> Material FloatingActionButton"
```

---

### Task 9: sephiroth HListView → 水平 RecyclerView

**Files:**
- Modify: `app/src/main/res/layout/activity_image_process.xml`
- Modify: `app/src/main/java/com/stickercamera/app/camera/ui/PhotoProcessActivity.java`
- Rewrite: `app/src/main/java/com/stickercamera/app/camera/adapter/FilterAdapter.java`
- Rewrite: `app/src/main/java/com/stickercamera/app/camera/adapter/StickerToolAdapter.java`

**Interfaces:**
- Produces: `FilterAdapter` / `StickerToolAdapter` 提供 `setOnItemClickListener(OnItemClickListener)`,回调签名 `void onItemClick(int position)`;`FilterAdapter` 保留 `getSelectFilter()`/`setSelectFilter(int)`。

> 观感微调:放弃 `hlv_dividerWidth` 的 15px 分隔(功能优先,可接受)。

- [ ] **Step 1: 布局替换**

`activity_image_process.xml` 把 `it.sephiroth.android.library.widget.HListView`(id `list_tools`)替换为:

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/list_tools"
    android:layout_width="match_parent"
    android:layout_height="fill_parent"
    android:background="@color/common_background"
    android:overScrollMode="always" />
```

(删除 `app:hlv_dividerWidth` 与 `android:gravity`。)

- [ ] **Step 2: 重写 `FilterAdapter.java`**

```java
package com.stickercamera.app.camera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.skykai.stickercamera.R;
import com.stickercamera.app.camera.effect.FilterEffect;
import com.stickercamera.app.camera.util.GPUImageFilterTools;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.EffectHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<FilterEffect> filterUris;
    private final Context mContext;
    private final Bitmap background;
    private int selectFilter = 0;
    private OnItemClickListener onItemClickListener;

    public FilterAdapter(Context context, List<FilterEffect> effects, Bitmap background) {
        this.mContext = context;
        this.filterUris = effects;
        this.background = background;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public void setSelectFilter(int selectFilter) {
        this.selectFilter = selectFilter;
    }

    public int getSelectFilter() {
        return selectFilter;
    }

    public FilterEffect getItem(int position) {
        return filterUris.get(position);
    }

    @NonNull
    @Override
    public EffectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_filter, parent, false);
        return new EffectHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EffectHolder holder, int position) {
        final FilterEffect effect = getItem(position);
        holder.filteredImg.setImage(background);
        holder.filterName.setText(effect.getTitle());
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(mContext, effect.getType());
        holder.filteredImg.setFilter(filter);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterUris.size();
    }

    static class EffectHolder extends RecyclerView.ViewHolder {
        GPUImageView filteredImg;
        TextView filterName;

        EffectHolder(View itemView) {
            super(itemView);
            filteredImg = itemView.findViewById(R.id.small_filter);
            filterName = itemView.findViewById(R.id.filter_name);
        }
    }
}
```

- [ ] **Step 3: 重写 `StickerToolAdapter.java`**

```java
package com.stickercamera.app.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.common.util.ImageLoaderUtils;
import com.github.skykai.stickercamera.R;
import com.stickercamera.app.model.Addon;

import java.util.List;

public class StickerToolAdapter extends RecyclerView.Adapter<StickerToolAdapter.EffectHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<Addon> filterUris;
    private final Context mContext;
    private OnItemClickListener onItemClickListener;

    public StickerToolAdapter(Context context, List<Addon> effects) {
        this.mContext = context;
        this.filterUris = effects;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public Addon getItem(int position) {
        return filterUris.get(position);
    }

    @NonNull
    @Override
    public EffectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_tool, parent, false);
        return new EffectHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EffectHolder holder, int position) {
        final Addon sticker = getItem(position);
        holder.container.setVisibility(View.GONE);
        ImageLoaderUtils.displayDrawableImage(sticker.getId() + "", holder.logo, null);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterUris.size();
    }

    static class EffectHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        ImageView container;

        EffectHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.effect_image);
            container = itemView.findViewById(R.id.effect_background);
        }
    }
}
```

- [ ] **Step 4: 改 PhotoProcessActivity**

字段与 import:删 `import it.sephiroth.android.library.widget.HListView;`,新增 `import androidx.recyclerview.widget.RecyclerView;` 和 `import androidx.recyclerview.widget.LinearLayoutManager;`(PhotoProcessActivity 原本用的是 HListView,Task 5 未给它引入 RecyclerView);字段 `@BindView(R.id.list_tools) HListView bottomToolBar;` 的类型 `HListView` → `RecyclerView`(注解不变)。同时删除文件里残留的 `import android.widget.GridView;` 若未被使用(由编译器报未用 import 时清理,非必须)。

`initStickerToolBar()` 改为:

```java
private void initStickerToolBar() {
    bottomToolBar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    StickerToolAdapter adapter = new StickerToolAdapter(PhotoProcessActivity.this, EffectUtil.addonList);
    adapter.setOnItemClickListener(position -> {
        labelSelector.hide();
        Addon sticker = EffectUtil.addonList.get(position);
        EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
                new EffectUtil.StickerCallback() {
                    @Override
                    public void onRemoveSticker(Addon sticker) {
                        labelSelector.hide();
                    }
                });
    });
    bottomToolBar.setAdapter(adapter);
    setCurrentBtn(stickerBtn);
}
```

`initFilterToolBar()` 改为:

```java
private void initFilterToolBar() {
    final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();
    bottomToolBar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    final FilterAdapter adapter = new FilterAdapter(PhotoProcessActivity.this, filters, smallImageBackgroud);
    adapter.setOnItemClickListener(position -> {
        labelSelector.hide();
        if (adapter.getSelectFilter() != position) {
            adapter.setSelectFilter(position);
            GPUImageFilter filter = GPUImageFilterTools.createFilterForType(
                    PhotoProcessActivity.this, filters.get(position).getType());
            mGPUImageView.setFilter(filter);
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
            if (mFilterAdjuster.canAdjust()) {
                //mFilterAdjuster.adjust(100);
            }
        }
    });
    bottomToolBar.setAdapter(adapter);
}
```

- [ ] **Step 5: 验证无残留**

Run: `grep -rn "sephiroth\|HListView" app/src && echo "STILL OLD" || echo "CLEAN"`
Expected: `CLEAN`

- [ ] **Step 6: Commit**

```bash
git add app/src
git commit -m "refactor(app): HListView -> 水平 RecyclerView,适配器改 RecyclerView.Adapter"
```

---

### Task 10: systembartint 移除 + edge-to-edge 适配

**Files:** `app/src/main/java/com/stickercamera/base/BaseActivity.java`

- [ ] **Step 1: 重写 BaseActivity 的窗口/状态栏处理**

删除 import:`android.annotation.TargetApi`、`android.os.Build`、`android.util.TypedValue`、`android.view.WindowManager`、`com.readystatesoftware.systembartint.SystemBarTintManager`。
新增 import(`android.view.View` 原文件已有,勿重复):

```java
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
```

`onCreate` 里把 `initWindow();` 保留;`initWindow()` 改为:

```java
private void initWindow() {
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
}
```

删除 `getStatusBarColor()` 方法(原仅供 tint 使用);`getColorPrimary()` 保留。

`setContentView(int)` 改为(在 super 之后、titleBar 绑定之前插入 inset 处理):

```java
@Override
public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    final View content = findViewById(android.R.id.content);
    if (content != null) {
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
    titleBar = (CommonTitleBar) findViewById(R.id.title_layout);
    if (titleBar != null)
        titleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
}
```

- [ ] **Step 2: 验证无残留**

Run: `grep -rn "systembartint\|SystemBarTint" app/src && echo "STILL OLD" || echo "CLEAN"`
Expected: `CLEAN`

- [ ] **Step 3: Commit**

```bash
git add app/src
git commit -m "refactor(app): 移除 systembartint,改 edge-to-edge + WindowInsets 适配"
```

---

### Task 11: 全量构建绿灯

- [ ] **Step 1: clean + assembleDebug**

Run: `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew clean assembleDebug`
Expected: `BUILD SUCCESSFUL`,产出 `app/build/outputs/apk/debug/app-debug.apk`

- [ ] **Step 2: 迭代修编译错误**

若有报错(常见:遗漏的 import、ButterKnife 与 AGP 8 注解处理冲突、个别 androidx 类名),逐个修复后重跑 Step 1。
**ButterKnife 兜底:** 若 ButterKnife 10.2.3 与 AGP 8/Java 17 注解处理无法通过,停止并与用户确认改用 ViewBinding(见 spec 风险 1),不要私自大改。

- [ ] **Step 3: Commit(若有修复)**

```bash
git add -A
git commit -m "fix: 修复 AGP 8 全量构建编译问题"
```

---

### Task 12: 模拟器冒烟测试

- [ ] **Step 1: 启动模拟器并安装**

```bash
adb devices
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew installDebug
adb shell am start -n com.github.skykai.stickercamera/com.stickercamera.app.ui.MainActivity
```

- [ ] **Step 2: 走主流程并观察**

拍照 → 裁剪 → 进入图片处理 → 切滤镜(验证 GPU `.so` 加载、滤镜生效)→ 加贴纸(工具条横向滚动+点击)→ 加标签 → 保存 → 返回主界面展示。
用 `adb logcat` 观察有无崩溃 / `UnsatisfiedLinkError`(.so)/ inset 遮挡。

- [ ] **Step 3: 修正 edge-to-edge 遮挡(如有)**

若某全屏界面(相机/裁剪/处理)控件被状态栏或导航栏遮挡或留白异常,针对该界面根布局微调 inset 应用(只动受影响界面)。重装验证。

- [ ] **Step 4: Commit(若有修复)**

```bash
git add -A
git commit -m "fix: 模拟器冒烟回归修正"
```

---

## 验收标准

- `./gradlew clean assembleDebug` 成功产出 APK。
- 模拟器主流程跑通,GPU 滤镜生效,无崩溃。
- `grep -rn "android.support\|de.greenrobot\|sephiroth\|melnykov\|systembartint\|InjectView" app/src` 为空。
- 全程在 `modernize-build-toolchain` 分支。
