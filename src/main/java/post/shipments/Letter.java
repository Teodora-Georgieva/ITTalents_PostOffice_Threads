package post.shipments;

import post.people.CitizenData;

public class Letter extends Shipment{
    public Letter(CitizenData senderData, CitizenData receiverData) {
        super(senderData, receiverData);
        this.addStatus(Status.NONE);
    }
    /*
    public Letter(int senderId, int receiverId) {
        super(senderId, receiverId);
        this.addStatus(Status.NONE);
    }

     */

    @Override
    public ShipmentType getShipmentType() {
        return ShipmentType.LETTER;
    }

    @Override
    public double getTax() {
        return 0.5;
    }
}