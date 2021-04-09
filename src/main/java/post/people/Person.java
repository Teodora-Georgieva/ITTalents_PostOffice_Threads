package post.people;

import post.Post;
import post.Town;
import post.utils.Validator;

public abstract class Person extends Thread{
    public enum PersonType{
        POSTMAN, CITIZEN
    }

    private static int id = 1;
    private int personID;
    private String firstName;
    private String lastName;
    public static Town town;
    public static Post post;

    public Person(String firstName, String lastName) {
        super(firstName + " " + lastName);
        this.personID = id++;
        if(Validator.isValidString(firstName)) {
            this.firstName = firstName;
        }
        else{
            this.firstName = "Ivan";
        }

        if(Validator.isValidString(lastName)) {
            this.lastName = lastName;
        }
        else{
            this.lastName = "Ivanov";
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public abstract PersonType getPersonType();

    public int getPersonID() {
        return this.personID;
    }
}