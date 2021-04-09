package post.people;

public class CitizenData{
    private int id;
    private String firstName;
    private String lastName;
    private String address;

    public CitizenData(int id, String firstName, String lastName, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CitizenData{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}