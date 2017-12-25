package com.whut.entity;

/**
 * @author Bruce
 * @date 2017/11/27
 */
public class Ganji {

    private String company;
    private String address;
    private String phone;
    private String title;
    private String category;
    private String publishTime;
    private String crawlTime;
    private String city;

    private String website;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(String crawlTime) {
        this.crawlTime = crawlTime;
    }


    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Ganji{" +
                "company='" + company + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", crawlTime='" + crawlTime + '\'' +
                '}';
    }

}
