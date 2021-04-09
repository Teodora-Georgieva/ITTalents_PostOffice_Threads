package post.people;

import post.shipments.Shipment;
import post.utils.Generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Postman extends Person{
    public enum JobTitle{
        POSTMAN, LETTER_PICKER;
    }

    int experience;
    private int countOfShipments; //todo
    private boolean isFree;
    private ArrayList<Shipment> shipments;

    public Postman(String firstName, String lastName) {
        super(firstName, lastName);
        this.experience = Generator.generateRandomNumber(3, 10);
        this.shipments = new ArrayList<>();
        this.isFree = true;
    }

    @Override
    public PersonType getPersonType() {
        return PersonType.POSTMAN;
    }

    public JobTitle getJobTitle(){
        return JobTitle.POSTMAN;
    }

    public  boolean isFree(){
        return this.isFree;
    }

    @Override
    public void run() {
        while (true){
            if(this.isFree) {
                ArrayList<Shipment> shipments = post.deliverShipments();
                this.addShipments(shipments);
                for(Shipment shipment : this.shipments){
                    shipment.setPostmanID(this.getPersonID());
                }
            }
            else{
                System.out.println("Postman " + this.getFirstName() + " " + this.getLastName() + " is not free now");
                continue;
            }

            this.isFree = false;//already not free
            this.deliverEachShipmentsToAddresses(this.shipments);
            this.isFree = true;
        }
    }

    private void addShipments(ArrayList<Shipment> shipments){
        this.shipments.addAll(shipments);
    }

    private void deliverEachShipmentsToAddresses(ArrayList<Shipment> shipments){
        for (int i = 0; i < shipments.size(); i++) {
            Shipment shipment = shipments.remove(i);
            Citizen receiver = findReceiver(shipment.getReceiverAddress());
            receiver.receiveShipment(shipment);
            if(shipment.getShipmentType() == Shipment.ShipmentType.LETTER){
                try {
                    Thread.currentThread().sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("A letter is delivered by " + Thread.currentThread().getName() + " to " + receiver.getFirstName()
                             + " " + receiver.getLastName());
            }
            else{
                try {
                    Thread.currentThread().sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("A package is delivered by " + Thread.currentThread().getName() + " to " +
                                   receiver.getFirstName() + " " + receiver.getLastName());
            }
        }
        this.shipments = new ArrayList<>();
    }

    private Citizen findReceiver(String address){
        Collection<Citizen> allCitizens = town.getCitizens();
        for(Citizen citizen : allCitizens){
            if(address.equals(citizen.getAddress())){
                return citizen;
            }
        }

        return null;
    }
}