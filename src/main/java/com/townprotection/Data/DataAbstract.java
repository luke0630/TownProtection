package com.townprotection.Data;

import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Data.SelectorData.SelectorData;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DataAbstract {
    public void setSelectorData(SelectorData selectorData) {
        this.selectorData = selectorData;
    }

    SelectorData selectorData = new SelectorData();
    List<UUID> manager = new ArrayList<>(); //市長よりもえらくない町の権限を持った人
    public List<ActionList.Action> allowActionList = new ArrayList<>(); //町の中で許されているアクション(アクションが起きた場所が土地の範囲だった場合、土地のルールが適用されます)
    UUID owner;
    String name = "";
    Material icon;
    CreationDate creationDate;

    public CreationDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(CreationDate creationDate) {
        this.creationDate = creationDate;
    }


    public void setManager(List<UUID> manager) {
        this.manager = manager;
    }

    public void setAllowActionList(List<ActionList.Action> allowActionList) {
        this.allowActionList = allowActionList;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public DataAbstract(Material icon, String name, CreationDate date) {
        // 年、月、日をそれぞれ取得
        this.icon = icon;
        this.name = name;
        this.creationDate = date;
    }

    public List<UUID> getManager() {
        return manager;
    }

    public List<ActionList.Action> getAllowActionList() {
        return allowActionList;
    }

    public UUID getOwner() {
        return owner;
    }

    public Material getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public SelectorData getSelectorData() {
        return selectorData;
    }

    public static class CreationDate {
        private int year = 2024;
        private int month = 1;
        private int day = 1;

        public CreationDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public Integer getDifferenceByDay(CreationDate date) {
            var diffYear = this.year - date.getYear();
            var diffMonth = this.month - date.getMonth();
            var diffDay = this.day - date.getDay();

            return diffDay + (diffMonth * 30) + (diffYear * 360);
        }

        public int getDay() {
            return day;
        }


        public void setDay(int day) {
            this.day = day;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}