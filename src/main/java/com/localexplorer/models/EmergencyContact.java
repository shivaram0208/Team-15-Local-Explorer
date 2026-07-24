package com.localexplorer.models;

public class EmergencyContact {
    private int contactId;
    private int userId;
    private String label;
    private String phone;

    public EmergencyContact() {}

    public EmergencyContact(int contactId, int userId, String label, String phone) {
        this.contactId = contactId;
        this.userId = userId;
        this.label = label;
        this.phone = phone;
    }

    public int getContactId() { return contactId; }
    public void setContactId(int contactId) { this.contactId = contactId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
