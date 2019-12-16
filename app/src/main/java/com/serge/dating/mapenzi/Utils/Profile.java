package com.serge.dating.mapenzi.Utils;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable {

    private List<Images> images;

    private String aboutMe;
    private String jobTitle;
    private String companyName;
    private String schoolName;
    private String gender;

    private boolean showAge;
    private boolean showDistance;

    private boolean sports;
    private boolean travel;
    private boolean music;
    private boolean fishing;

    private String description;
    private String sex;
    private String preferSex;
    private String dateOfBirth;

    public Profile() {
    }
    public Profile(String aboutMe, String jobTitle, String companyName, String schoolName, String gender, boolean showAge, boolean showDistance, boolean sports, boolean travel, boolean music, boolean fishing, String description, String sex, String preferSex, String dateOfBirth) {
        this.aboutMe = aboutMe;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.schoolName = schoolName;
        this.gender = gender;
        this.showAge = showAge;
        this.showDistance = showDistance;
        this.sports = sports;
        this.travel = travel;
        this.music = music;
        this.fishing = fishing;
        this.description = description;
        this.sex = sex;
        this.preferSex = preferSex;
        this.dateOfBirth = dateOfBirth;
    }

    public Profile(List<Images> images, String aboutMe, String jobTitle, String companyName, String schoolName, String gender, boolean showAge, boolean showDistance, boolean sports, boolean travel, boolean music, boolean fishing, String description, String sex, String preferSex, String dateOfBirth) {
        this.images = images;
        this.aboutMe = aboutMe;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.schoolName = schoolName;
        this.gender = gender;
        this.showAge = showAge;
        this.showDistance = showDistance;
        this.sports = sports;
        this.travel = travel;
        this.music = music;
        this.fishing = fishing;
        this.description = description;
        this.sex = sex;
        this.preferSex = preferSex;
        this.dateOfBirth = dateOfBirth;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isShowAge() {
        return showAge;
    }

    public void setShowAge(boolean showAge) {
        this.showAge = showAge;
    }

    public boolean isShowDistance() {
        return showDistance;
    }

    public void setShowDistance(boolean showDistance) {
        this.showDistance = showDistance;
    }


    public boolean isSports() {
        return sports;
    }

    public void setSports(boolean sports) {
        this.sports = sports;
    }

    public boolean isTravel() {
        return travel;
    }

    public void setTravel(boolean travel) {
        this.travel = travel;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isFishing() {
        return fishing;
    }

    public void setFishing(boolean fishing) {
        this.fishing = fishing;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPreferSex() {
        return preferSex;
    }

    public void setPreferSex(String preferSex) {
        this.preferSex = preferSex;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
