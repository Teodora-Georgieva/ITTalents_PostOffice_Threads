package post.shipments;

import post.people.CitizenData;
import post.utils.Generator;

public class Package extends Shipment{
    private boolean isFragile;
    private boolean isOnerous;
    private int length;
    private int height;
    private int width;


    public Package(CitizenData senderData, CitizenData receiverData, boolean isFragile) {
        super(senderData, receiverData);
        this.height = Generator.generateRandomSize();
        this.length = Generator.generateRandomSize();
        this.width = Generator.generateRandomSize();
        this.isFragile = isFragile;
        if(this.isFragile){
            this.addStatus(Status.FRAGILE);
        }
        if(this.length > 60 || this.height > 60 || this.width > 60){
            this.isOnerous = true;
            this.addStatus(Status.ONEROUS);
        }

        if(!this.isOnerous && !this.isFragile){
            this.addStatus(Status.NORMAL);
        }
    }

    /*
    public Package(int senderId, int receiverId, boolean isFragile) {
        super(senderId, receiverId);
        this.height = Generator.generateRandomSize();
        this.length = Generator.generateRandomSize();
        this.width = Generator.generateRandomSize();
        this.isFragile = isFragile;
        if(this.isFragile){
            this.addStatus(Status.FRAGILE);
        }
        if(this.length > 60 || this.height > 60 || this.width > 60){
            this.isOnerous = true;
        }

        if(this.isOnerous){
            this.addStatus(Status.ONEROUS);
        }

        if(!this.isOnerous && !this.isFragile){
            this.addStatus(Status.NORMAL);
        }
    }

     */

    @Override
    public ShipmentType getShipmentType() {
        return ShipmentType.PACKAGE;
    }

    @Override
    public double getTax() {
        double mainTax = 2.0;
        double taxToPay = mainTax;
        if(this.isFragile){
            taxToPay += mainTax/2;
        }

        if(this.isOnerous){
            taxToPay += mainTax/2;
        }
        return taxToPay;
    }
}