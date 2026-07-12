/**
 * Represents a Customer of the Bank.
 * Encapsulates demographic, contact, and identity details (Aadhaar/PAN).
 * Highlights the OOP concept of Encapsulation.
 * 
 * Part of the Banking Information System Project.
 */
public class Customer {
    private String name;
    private int age;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String aadhaarNumber;
    private String panNumber;

    /**
     * Parameterized Constructor to initialize all customer attributes.
     */
    public Customer(String name, int age, String gender, String phoneNumber, String email, String address, String aadhaarNumber, String panNumber) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.aadhaarNumber = aadhaarNumber;
        this.panNumber = panNumber;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    /**
     * Serializes customer details into a pipe-delimited format for file persistence.
     * 
     * @return Pipe-separated values of customer fields
     */
    public String serialize() {
        return name + "|" + age + "|" + gender + "|" + phoneNumber + "|" + email + "|" + address + "|" + aadhaarNumber + "|" + panNumber;
    }

    /**
     * Deserializes pipe-delimited text into a Customer object.
     * 
     * @param data Pipe-delimited string
     * @return Deserialized Customer object
     */
    public static Customer deserialize(String data) {
        String[] parts = data.split("\\|");
        String name = parts[0];
        int age = Integer.parseInt(parts[1]);
        String gender = parts[2];
        String phone = parts[3];
        String email = parts[4];
        String address = parts[5];
        String aadhaar = parts[6];
        String pan = parts[7];
        return new Customer(name, age, gender, phone, email, address, aadhaar, pan);
    }

    @Override
    public String toString() {
        return "Customer Name : " + name + 
             "\nAge / Gender  : " + age + " / " + gender +
             "\nPhone Number  : " + phoneNumber + 
             "\nEmail         : " + email + 
             "\nAddress       : " + address +
             "\nAadhaar Card  : " + aadhaarNumber +
             "\nPAN Card      : " + panNumber;
    }
}
