package com.DJACompany.djattendance;

public class Room {
    private int capacity, availability;
    private String roomName, roomBranch, rent;

    private boolean is_chair, is_table, is_fan, is_wardbrobe;
    String getRoomName() {
        return roomName;
    }
    void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    void setRoomBranch(String roomBranch) {
        this.roomBranch = roomBranch;
    }
    String getRoomBranch() {
        return this.roomBranch;
    }

    String getRent() {
        return rent;
    }
    void setRent(String rent) {
        this.rent = rent;
    }
    void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    void setAvailability(int availability) {
        this.availability = availability;
    }
    int getCapacity() {
        return capacity;
    }
    int getavailability() {
        return this.availability;
    }

    void setIs_table(boolean table) {
        this.is_table = table;
    }

    void setIs_chair(boolean chair) {
        this.is_chair = chair;
    }
    void setIs_fan(boolean fan) {
        this.is_fan = fan;
    }
    void setIs_wardbrobe(boolean wardbrobe) {
        this.is_wardbrobe = wardbrobe;
    }

    boolean get_chair(){return is_chair;}
    boolean get_table(){return is_table;}
    boolean get_fan(){return is_fan;}
    boolean get_wardbrobe(){return is_wardbrobe;}
}