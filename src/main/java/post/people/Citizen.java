package post.people;

import post.MailBox;
import post.Post;
import post.shipments.Letter;
import post.shipments.Package;
import post.shipments.Shipment;
import post.utils.Generator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class Citizen extends Person{
    private String address;
    private ArrayList<Shipment> receivedShipments;
    private CitizenData citizenData;
    private double money;

    public Citizen(String firstName, String lastName) {
        super(firstName, lastName);
        this.address = "Address" + this.getPersonID();
        this.receivedShipments = new ArrayList<>();
        this.citizenData = new CitizenData(this.getPersonID(), this.getFirstName(), this.getLastName(), this.address);
        this.money = 10000;
    }

    public CitizenData getCitizenData() {
        return this.citizenData;
    }

    @Override
    public PersonType getPersonType() {
        return PersonType.CITIZEN;
    }

    public void receiveShipment(Shipment shipment){
        this.receivedShipments.add(shipment);
    }

    private void spendMoney(double money){
        if(this.money < money){
            this.money = 10000;
        }

        this.money -= money;
    }

    @Override
    public void run() {
        while (true) {
            int choice = Generator.generateRandomNumber(1, 3);
            int numOfShipments = Generator.generateRandomNumber(1, 10);
            switch (choice){
                case 1: this.sendLettersToMailBox(numOfShipments); break;
                case 2: this.sendLettersToPost(numOfShipments);break;

                case 3: this.sendPackages(numOfShipments); break;
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(numOfShipments + " shipments sent by citizen " + this.getFirstName() + " " + this.getLastName());
        }
    }

    private void sendLettersToMailBox(int numOfLetters){
        MailBox mailBox = town.giveMailBoxToCitizen();
        for (int i = 0; i < numOfLetters; i++) {
            /*
            int receiverId;
            do {
                receiverId = post.getRandomCitizenId();
            }while (receiverId == this.getPersonID());

             */
            Citizen receiver = null;
            do{
                receiver = town.getRandomCitizen();
            } while(receiver.getPersonID() == this.getPersonID());

            //mailBox.addLetter(new Letter(this.getPersonID(), receiverId));
            mailBox.addLetter(new Letter(this.getCitizenData(), receiver.getCitizenData()));
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendLettersToPost(int numberOfLetters){
        double money = 0;
        ArrayList<Shipment> letters = new ArrayList<>();
        for (int i = 0; i < numberOfLetters; i++) {
            /*
            int receiverId;
            do {
                receiverId = post.getRandomCitizenId();
            }while (receiverId == this.getPersonID());

             */
            Citizen receiver = null;
            do{
                receiver = town.getRandomCitizen();
            } while(receiver.getPersonID() == this.getPersonID());

            //Letter letter = new Letter(this.getPersonID(),receiverId);
            Letter letter = new Letter(this.getCitizenData(), receiver.getCitizenData());
            letters.add(letter);
            money+=letter.getTax();
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        post.receiveLettersFromCitizens(letters);
        post.addToArchive(LocalDate.now(), LocalTime.now(), letters);
        this.spendMoney(money);
        post.receiveMoney(money);
    }

    private void sendPackages(int numOfPackages){
        double money = 0;
        ArrayList<Shipment> packages = new ArrayList<>();
        for (int i = 0; i < numOfPackages; i++) {
            /*
            int receiverId;
            do {
                receiverId = post.getRandomCitizenId();
            }while (receiverId == this.getPersonID());
             */

            Citizen receiver = null;
            do{
                receiver = town.getRandomCitizen();
            } while(receiver.getPersonID() == this.getPersonID());

            //Package pack = new Package(this.getPersonID(), receiverId, false);
            boolean isFragile = new Random().nextBoolean() ? true : false;
            Package pack = new Package(this.getCitizenData(), receiver.getCitizenData(), isFragile);
            packages.add(pack);
            money += pack.getTax();
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        post.receivePackages(packages);
        post.addToArchive(LocalDate.now(), LocalTime.now(), packages);
        this.spendMoney(money);
        post.receiveMoney(money);
    }

    public String getAddress() {
        return this.address;
    }
}