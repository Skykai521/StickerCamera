# StickerCamera 编译链现代化设计

- 日期: 2026-06-18
- 状态: 已批准设计,待写实现计划
- 作者: SkyKai + Claude Code

## 背景

StickerCamera 是 2015 年的 Android 工程,编译链停留在 AGP 1.2.3 / Gradle 2.4 / compileSdk 22,依赖 `jcenter()`(2021 年已停服)、`android.support.*`(已 EOL)、Retrolambda、ButterKnife 6.1.0,以及若干已废弃的 jcenter UI 库。本工程当前无法在现代开发机上构建。

开发机环境已就绪:JDK 8/11/17(默认 17),Android SDK 含 `android-36` 平台与 `build-tools 36.0.0`,NDK 已装。

## 目标

把整个工程的编译链升级到现代、可长期维护的版本,完成 AndroidX 全面迁移,使其能在当前开发机上 `./gradlew assembleDebug` 构建通过,并保持原有功能。

### 目标版本(已确认)

| 项 | 现状 | 目标 |
|---|---|---|
| JDK(运行 Gradle) | — | 17 |
| Java source/target | Retrolambda 模拟 8 | 17(移除 Retrolambda) |
| Gradle wrapper | 2.4 | 8.13 |
| AGP | 1.2.3 | 8.11.1 |
| compileSdk | 22 | 36 |
| targetSdk | 22 | 36 |
| minSdk | 15 | 26 |
| 仓库 | `jcenter()` | `google()` + `mavenCentral()` |
| AndroidX | support 库 | `android.useAndroidX=true` 全迁移 |

> 备注:AGP 8.11.x 是稳定支持 compileSdk 36 的版本线,配对 Gradle 8.13。若实现时该精确版本号有微调,以"能稳定支持 compileSdk 36 的最近 AGP 8.11+/配套 Gradle"为准。

## 范围

### 范围内

1. 构建脚本与 wrapper 全面升级(根、settings、gradle.properties、三模块、wrapper、local.properties)。
2. `android.support.*` → `androidx.*` 全迁移(java + xml)。
3. ButterKnife 6.1.0 → 10.2.3(保留 ButterKnife)。
4. EventBus 2.4.0 → 3.3.1。
5. 替换 3 个仍在用的废弃 UI 库:melnykov FAB、sephiroth HListView、systembartint。
6. 删除未使用的 rengwuxian MaterialEditText 依赖。
7. Gpu-Image 原生 `.so` 迁移到 `jniLibs` 并裁剪废弃 ABI。
8. 本地 jar(fastjson、UIL)切换为 mavenCentral 坐标。
9. 移除 Retrolambda。
10. targetSdk 36 带来的强制 edge-to-edge 适配(各界面 WindowInsets)。

### 范围外(本次不做)

- 不替换 UIL 为 Glide、不替换 fastjson 为 fastjson2(仅升版本/换坐标,API 不变)。
- 不重构 app 业务架构、不动自定义控件(贴纸/标签引擎)逻辑。
- 不引入 Kotlin、不引入 View Binding(除非 ButterKnife 与 AGP 8 冲突,见风险)。
- 不做单元测试补全(工程现仅有空的 instrumentation 桩)。

## 详细设计

### 1. 构建文件

- **根 `build.gradle`**:删除 `me.tatarka:gradle-retrolambda` classpath;AGP `1.2.3` → `8.11.1`;移除 `buildscript`/`allprojects` 里的 `jcenter()`(仓库改到 settings 集中声明)。
- **`settings.gradle`**:新增 `pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }` 与 `dependencyResolutionManagement { repositories { google(); mavenCentral() } }`;保留 `include ':app', ':Gpu-Image', ':ImageViewTouch'`。
- **`gradle.properties`**:新增 `android.useAndroidX=true`、`android.nonTransitiveRClass=true`;设置合理 `org.gradle.jvmargs`(如 `-Xmx2048m -Dfile.encoding=UTF-8`)。
- **`gradle/wrapper/gradle-wrapper.properties`**:`distributionUrl` → `gradle-8.13-all.zip`。
- **`local.properties`**:写入 `sdk.dir=/Users/skykai/Library/Android/sdk`(该文件不纳入 git)。
- **各模块 `build.gradle`**:
  - `apply plugin` 形式保留或转 `plugins {}` 均可,但移除 `me.tatarka.retrolambda`。
  - `android { namespace "<pkg>" }`,并从对应 `AndroidManifest.xml` 删除 `package=` 属性。
  - `compileSdk 36`;`:app` 设 `minSdk 26 / targetSdk 36`,库模块设 `minSdk 26`。
  - `buildToolsVersion` 可省略(AGP 自动选默认)。
  - `compileOptions { sourceCompatibility JavaVersion.VERSION_17; targetCompatibility JavaVersion.VERSION_17 }`。
  - 依赖配置 `compile` → `implementation`(模块间 `compile project(...)` → `implementation project(...)`;库模块对外暴露的 API 用 `api`)。
  - 启用 `buildFeatures { }` 视需要(本次无需 viewBinding,除非回退方案触发)。

各模块 namespace:
- `:app` → `com.github.skykai.stickercamera`
- `:Gpu-Image` → `jp.co.cyberagent.android.gpuimage`
- `:ImageViewTouch` → `com.imagezoom`(已确认;该模块为标准 `src/main` 布局,无需 sourceSets 调整,仅需加 namespace、删 manifest 的 `package`)

### 2. AndroidX 迁移(实测范围)

仅 7 个 java 文件 + 3 处 XML 含 `android.support.*`,均为 1:1 改名:

| 旧 | 新 |
|---|---|
| `android.support.v7.app.AppCompatActivity` | `androidx.appcompat.app.AppCompatActivity` |
| `android.support.v4.app.Fragment` / `FragmentActivity` / `FragmentManager` / `FragmentPagerAdapter` | `androidx.fragment.app.*` |
| `android.support.v4.view.ViewPager`(含 `OnPageChangeListener`) | `androidx.viewpager.widget.ViewPager` |
| `android.support.v4.widget.SwipeRefreshLayout` | `androidx.swiperefreshlayout.widget.SwipeRefreshLayout` |
| `android.support.v7.widget.RecyclerView` / `LinearLayoutManager` | `androidx.recyclerview.widget.*` |
| `android.support.v7.widget.CardView` | `androidx.cardview.widget.CardView` |
| `android.support.annotation.Nullable` | `androidx.annotation.Nullable` |

XML 中 3 处全限定标签同步改名:`android.support.v4.view.ViewPager`、`android.support.v7.widget.CardView`、`android.support.v7.widget.RecyclerView`。

### 3. ButterKnife 6.1.0 → 10.2.3

- 依赖:`implementation 'com.jakewharton:butterknife:10.2.3'` + `annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'`。
- 代码:`@InjectView` → `@BindView`(6 文件共 30 处);`ButterKnife.inject(...)` → `ButterKnife.bind(...)`(7 处,含 MainActivity 的 ViewHolder `bind(this, itemView)`);其它 ButterKnife 注解(如 `@OnClick`,若有)一并按 10.x API 调整。
- 注意:ButterKnife 10.x 要求 AndroidX,需先完成第 2 步。

### 4. EventBus 2.4.0 → 3.3.1

- 依赖:`implementation 'org.greenrobot:eventbus:3.3.1'`。
- 代码(2 文件):`import de.greenrobot.event.EventBus` → `org.greenrobot.eventbus.EventBus`;`register`/`unregister`/`post` API 不变;`public void onEventMainThread(FeedItem)` → 加注解 `@Subscribe(threadMode = ThreadMode.MAIN)`,方法名可改为 `onEvent`(MainActivity)。

### 5. 废弃 UI 库替换(功能优先,允许观感微调)

- **melnykov FAB → Material Components FAB**(`com.google.android.material:material:1.11.0`):
  - `activity_main.xml`:`com.melnykov.fab.FloatingActionButton` → `com.google.android.material.floatingactionbutton.FloatingActionButton`,属性按 Material API 调整。
  - `MainActivity.java`:导入与字段类型改为 Material FAB;放弃 `attachToRecyclerView` 的随滚动自动隐藏特性(Material FAB 无此 API)。
- **sephiroth HListView → 水平 RecyclerView**(本次最重一块):
  - `activity_image_process.xml`:`it.sephiroth.android.library.widget.HListView` → `androidx.recyclerview.widget.RecyclerView`。
  - `PhotoProcessActivity.java`:`bottomToolBar` 类型改为 `RecyclerView`,设置 `LinearLayoutManager(HORIZONTAL)`;`setOnItemClickListener` 改走 adapter 点击回调。
  - `FilterAdapter`、`StickerToolAdapter`:从 `BaseAdapter` 重写为 `RecyclerView.Adapter<VH>`(getView → onCreateViewHolder/onBindViewHolder,新增点击回调接口)。
- **systembartint → edge-to-edge + 状态栏着色**:
  - `BaseActivity.initWindow()`:移除 `FLAG_TRANSLUCENT_STATUS/NAVIGATION` 与 `SystemBarTintManager`;改用 `getWindow().setStatusBarColor(getColorPrimary())` 等价表达原"状态栏着主题色"意图。
- **rengwuxian MaterialEditText → 删除**:实测全工程无引用,直接从 `app/build.gradle` 删依赖,无代码改动。

### 6. Gpu-Image 原生库

- 将 `Gpu-Image/libs/<abi>/libgpuimage-library.so` 迁移到 `Gpu-Image/src/main/jniLibs/<abi>/`(或在 build.gradle 配 `sourceSets.main.jniLibs.srcDirs`)。
- 裁剪废弃 ABI:删除 `armeabi`、`mips`、`mips64`;保留 `arm64-v8a`、`armeabi-v7a`、`x86`、`x86_64`。
- 非标准 sourceSets(`src/`、`res/`)归位到 AGP 8 标准布局或显式声明 `sourceSets`;加 `namespace`,删 manifest 的 `package`。

### 7. 依赖坐标与版本映射

| 旧 | 新 |
|---|---|
| `com.android.support:appcompat-v7:22.2.0` | `androidx.appcompat:appcompat:1.6.1` |
| `com.android.support:recyclerview-v7:22.2.0` | `androidx.recyclerview:recyclerview:1.3.2` |
| `com.android.support:cardview-v7:22.2.0` | `androidx.cardview:cardview:1.0.0` |
| (新增) | `com.google.android.material:material:1.11.0` |
| `com.jakewharton:butterknife:6.1.0` | `com.jakewharton:butterknife:10.2.3` + compiler |
| `de.greenrobot:eventbus:2.4.0` | `org.greenrobot:eventbus:3.3.1` |
| `files('libs/fastjson-1.2.5.jar')` | `com.alibaba:fastjson:1.2.83` |
| `files('universal-image-loader-1.9.4.jar')` | `com.nostra13.universalimageloader:universal-image-loader:1.9.5` |
| `systembartint` / melnykov FAB / MaterialEditText / hlistview | 移除 |

> 上述 androidx/material 版本为兼容 minSdk 26 的稳定版;实现时若 AGP 8.11 要求更高的某传递依赖版本,以 Gradle 实际解析为准做最小上调。

### 8. targetSdk 36 的 edge-to-edge 适配

targetSdk ≥ 35 在 Android 15+/16 设备上强制 edge-to-edge,内容会延伸到系统栏后方。需:

- 在 `BaseActivity` 启用 `WindowCompat.setDecorFitsSystemWindows(window, false)` 并对内容根视图应用 `systemBars()` insets 作为 padding,避免标题栏/按钮被状态栏或导航栏遮挡。
- 全屏相机/裁剪预览(已是 FullScreen 主题)按需对覆盖控件(快门、返回等)应用 insets。
- 该适配与第 5 步 systembartint 的移除合并处理。

## 风险与缓解

1. **ButterKnife 10.2.3 + AGP 8 / Java 17 注解处理兼容性(最高风险)**:这是已知痛点。用户选择保留 ButterKnife,先按此实施;若构建无法通过,**回退方案为改用 ViewBinding**(届时再与用户确认,涉及各 Activity 注入代码改写)。
2. **edge-to-edge 遮挡**:targetSdk 36 下界面可能被系统栏遮挡,需逐界面验证 insets。
3. **HListView → RecyclerView 行为差异**:滤镜/贴纸工具条滚动与点击需实机回归。
4. **依赖解析**:个别旧坐标在 mavenCentral 的可用性与传递依赖版本冲突,以 Gradle 解析报错为准逐个修正。
5. **原生库加载**:ABI 裁剪后需在真机(arm64)与模拟器(x86_64)各验证一次滤镜功能,确保 `.so` 正确打包加载。

## 验证标准

- `./gradlew clean assembleDebug` 构建成功产出 APK。
- (可选)`./gradlew installDebug` 后实机/模拟器走通主流程:拍照 → 裁剪 → 滤镜 → 贴纸 → 标签 → 保存 → 主界面展示。
- GPU 滤镜实际生效(验证 `.so` 加载)。
- 各界面无被系统栏遮挡的可见问题。
