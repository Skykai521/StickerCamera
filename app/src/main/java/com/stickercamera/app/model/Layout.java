/**
 * Copyright (C) 2014 xiaobudian Inc.
 */
package com.stickercamera.app.model;


import com.common.util.StringUtils;

/**
 * @author tongqian.ni
 */
public class Layout {
    private String   category;
    private String   type;
    private String   fileName;
    private Position position;
    private Font     font;
    private boolean  vertical;
    private String   placeholder;
    private String   text;

    public String getText() {
        if (StringUtils.isEmpty(text)) {
            return getPlaceholder();
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public static class Position {

        private Integer top;
        private Integer left;
        private Integer bottom;
        private Integer right;

        public Position() {
        }

        public Position(Integer left, Integer top, Integer right, Integer bottom) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        public Integer getTop() {
            return top;
        }

        public void setTop(Integer top) {
            this.top = top;
        }

        public Integer getLeft() {
            return left;
        }

        public void setLeft(Integer left) {
            this.left = left;
        }

        public Integer getBottom() {
            return bottom;
        }

        public void setBottom(Integer bottom) {
            this.bottom = bottom;
        }

        public Integer getRight() {
            return right;
        }

        public void setRight(Integer right) {
            this.right = right;
        }

    }

    public static class Font {

        private String  family;
        private Integer size;
        private String  color;

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

    }
}
