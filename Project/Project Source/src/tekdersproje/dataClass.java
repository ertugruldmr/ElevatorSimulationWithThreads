/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tekdersproje;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ertuğrul Demir
 */
public class dataClass {

    ArrayBlockingQueue<passengerInfo> floorQue0 = new ArrayBlockingQueue<passengerInfo>(100);
    ArrayBlockingQueue<passengerInfo> floorQue1 = new ArrayBlockingQueue<passengerInfo>(100);
    ArrayBlockingQueue<passengerInfo> floorQue2 = new ArrayBlockingQueue<passengerInfo>(100);
    ArrayBlockingQueue<passengerInfo> floorQue3 = new ArrayBlockingQueue<passengerInfo>(100);
    ArrayBlockingQueue<passengerInfo> floorQue4 = new ArrayBlockingQueue<passengerInfo>(100);

    int floor0 = 0;
    int floor1 = 0;
    int floor2 = 0;
    int floor3 = 0;
    int floor4 = 0;

    int[] totalPassedPassengerSize = {0, 0, 0, 0, 0};

    Object lock_0 = new Object();
    Object lock_1 = new Object();
    Object lock_2 = new Object();
    Object lock_3 = new Object();
    Object lock_4 = new Object();

    //Produce and Consume Methods & Methods for Login Thread
    public void floorQue0_Produce(passengerInfo value) {
        //System.out.println("Produce:\n" + "Size: " + value.getPassengerSize() + " Target Floor:" + value.getTargetFloor());
        try {
            floorQue0.put(value);
            /*
            System.out.println("Eleman eklendi");

            System.out.println("Mevcut elemanlar");
            System.out.println("Produce !!!!!! Elemanlar");
            for (passengerInfo passenger_tmp : floorQue0) {
                System.out.println("Hedef kat: " + passenger_tmp.getTargetFloor() + ", Eleman Sayısı: " + passenger_tmp.getPassengerSize());
            }
            System.out.println("Elemanlar bitti\n\n");
             */
        } catch (InterruptedException ex) {
            Logger.getLogger(dataClass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public passengerInfo floorQue0_Consume() throws InterruptedException {
        return floorQue0.take();
    }

    public void floorQue1_Produce(passengerInfo value) {

        try {
            floorQue1.put(value);
        } catch (InterruptedException ex) {
            Logger.getLogger(dataClass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public passengerInfo floorQue1_Consume() throws InterruptedException {
        return floorQue1.take();
    }

    public void floorQue2_Produce(passengerInfo value) {

        try {
            floorQue2.put(value);
        } catch (InterruptedException ex) {
            Logger.getLogger(dataClass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public passengerInfo floorQue2_Consume() throws InterruptedException {
        if (floorQue2.size() == 0) {
            System.out.println("Problem!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return floorQue2.take();
    }

    public void floorQue3_Produce(passengerInfo value) {

        try {
            floorQue3.put(value);
        } catch (InterruptedException ex) {
            Logger.getLogger(dataClass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public passengerInfo floorQue3_Consume() throws InterruptedException {
        return floorQue3.take();
    }

    public void floorQue4_Produce(passengerInfo value) {

        try {
            floorQue4.put(value);
        } catch (InterruptedException ex) {
            Logger.getLogger(dataClass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public passengerInfo floorQue4_Consume() throws InterruptedException {
        return floorQue4.take();
    }

    public void increaseCurrentFloorPassengerSize(int movedPassengerSize, int targetFloor) {

        boolean isInRangeFloor = (0 <= targetFloor) && (4 >= targetFloor);
        if (isInRangeFloor) {

            if (targetFloor == 1) {
                floor1 += movedPassengerSize;
            }
            if (targetFloor == 2) {
                floor2 += movedPassengerSize;
            }
            if (targetFloor == 3) {
                floor3 += movedPassengerSize;
            }
            if (targetFloor == 4) {
                floor4 += movedPassengerSize;
            }

        } else {
            System.out.println("Target Floor is nor in range!!!");
        }

    }

    public void transportPassengersFromFloor0(int emptyPassengerSize) throws InterruptedException {

        passengerInfo passenger = floorQue0_Consume();

        if (passenger.getPassengerSize() > emptyPassengerSize) {

            //CASE --> asansör ağzına kadar olcu alırken. (Aldığımız parça bize fazla geldi. Artanı iade edicem.)
            //1)Calculate moving passenger size
            int newPassengerSize = passenger.getPassengerSize() - emptyPassengerSize;
            //2)LeftOver Passenger Size after transfer
            passengerInfo newPassenger = new passengerInfo(passenger.getTargetFloor(), newPassengerSize);
            //3)Add calculation of moved passenger size     
            increaseCurrentFloorPassengerSize(emptyPassengerSize, passenger.getTargetFloor());
            //4)add back to que calculation of did not move passenger size
            floorQue0_Produce(newPassenger);

        } else if (passenger.getPassengerSize() < emptyPassengerSize) {

            //CASE --> Asansörde boş yer olmasına rağmen yolcu yok iken. (Asansör tam dolmadı, dolana kadar yeni parça al.)
            increaseCurrentFloorPassengerSize(passenger.getPassengerSize(), passenger.getTargetFloor());// We taked all passengers on the floor0

            int newEmptyPassengerSize = emptyPassengerSize - passenger.getPassengerSize();//Size of empty passenger zone
            while (newEmptyPassengerSize == 0) {

                passengerInfo passenger2 = floorQue0_Consume();
                if (passenger2.getPassengerSize() > newEmptyPassengerSize) {
                    //Calculate will move passenger size
                    increaseCurrentFloorPassengerSize(emptyPassengerSize, passenger2.getTargetFloor()); // increase size of passenger on the floor 
                    int newPassenger2Size = passenger2.getPassengerSize() - newEmptyPassengerSize;// Calculate leftOver passenger size
                    newEmptyPassengerSize = 0;// consumed all empty zones (Break the loop)  elevator is full
                    //LeftOver Passenger Size after transfer AND add back the leftOver Passengers  to Que 
                    passengerInfo newPassenger2 = new passengerInfo(passenger2.getTargetFloor(), newPassenger2Size);//add back to que FOR leftOvers
                    floorQue0_Produce(newPassenger2);
                } else if (passenger2.getPassengerSize() < newEmptyPassengerSize) {
                    //Still there is empty zone in lift.
                    increaseCurrentFloorPassengerSize(passenger2.getPassengerSize(), passenger2.getTargetFloor());//Take all passengers
                    newEmptyPassengerSize = newEmptyPassengerSize - passenger2.getPassengerSize();// next loop will calculate 
                } else if (passenger2.getPassengerSize() == newEmptyPassengerSize) {
                    increaseCurrentFloorPassengerSize(passenger2.getPassengerSize(), passenger2.getTargetFloor());//Take all passengers
                    newEmptyPassengerSize = 0;
                }

            }

        } else if (passenger.getPassengerSize() == emptyPassengerSize) {
            increaseCurrentFloorPassengerSize(passenger.getPassengerSize(), passenger.getTargetFloor());
            emptyPassengerSize = 0;
        }

    }

    // Update to a QUE CODE BELOW
    //Methods for Elevator Thread (Login-take passengers Que0 to Elevator)[Que0 --> Elevator]
    public elevatorData TakePassengersFromQue0_to_Elevator(int sizeOfPassengerRequest) throws InterruptedException {
        //Control the range  for sizeOfPassengerRequest  before come here.(sizeOfPassengerRequest --> empty zone in the elevator)
        passengerInfo hasTakedPassengerPackage = floorQue0_Consume();
        //System.out.println("İlk alınan parça size:"+hasTakedPassengerPackage.getPassengerSize()+"  Request: "+sizeOfPassengerRequest);

        elevatorData ElevatorData = new elevatorData();
        if (hasTakedPassengerPackage.getPassengerSize() > sizeOfPassengerRequest) {
            //System.out.println("Koşul-1");
            //Too much passenger for elevator. (Set elevator full, send back liftOver passenger to Que)

            //1) make full elevator
            ElevatorData.appendPassenger(sizeOfPassengerRequest, hasTakedPassengerPackage.getTargetFloor());
            //2) send back liftOver passenger to Que
            int leftOverPassengerSize = hasTakedPassengerPackage.getPassengerSize() - sizeOfPassengerRequest;
            passengerInfo leftOverPassenger = new passengerInfo(hasTakedPassengerPackage.getTargetFloor(), leftOverPassengerSize);
            floorQue0_Produce(leftOverPassenger);
            
            System.out.println("Zemin kattan Asansöre alınan yolcu sayısı:"+sizeOfPassengerRequest );

        } else if (hasTakedPassengerPackage.getPassengerSize() < sizeOfPassengerRequest) {
            //System.out.println("Koşul-2");
            //Not enough for full the elevator. Take other passengerPackage.

            //1)Append the passengers
            ElevatorData.appendPassenger(hasTakedPassengerPackage.getPassengerSize(), hasTakedPassengerPackage.getTargetFloor());
            //sout
            //2)take package until will be enough
            int newSizeOfPassengerRequest = sizeOfPassengerRequest - hasTakedPassengerPackage.getPassengerSize();
            while (newSizeOfPassengerRequest == 0) {

                passengerInfo hasSecondTakedPassengerPackage = floorQue0_Consume();
                if (newSizeOfPassengerRequest < hasSecondTakedPassengerPackage.getPassengerSize()) {
                    //Too much passengers. Elevator is full, resend leftOver passengers package.

                    //1)do elevator full
                    ElevatorData.appendPassenger(newSizeOfPassengerRequest, hasSecondTakedPassengerPackage.getTargetFloor());
                    newSizeOfPassengerRequest = 0;

                    //2)resend passenger package
                    int leftOverPassengerSize = hasSecondTakedPassengerPackage.getPassengerSize() - newSizeOfPassengerRequest;
                    passengerInfo leftOverPassenger = new passengerInfo(hasSecondTakedPassengerPackage.getTargetFloor(), leftOverPassengerSize);
                    floorQue0_Produce(leftOverPassenger);

                } else if (newSizeOfPassengerRequest > hasSecondTakedPassengerPackage.getPassengerSize()) {
                    //Not enough passengers. take other packages.
                    ElevatorData.appendPassenger(hasSecondTakedPassengerPackage.getPassengerSize(), hasSecondTakedPassengerPackage.getTargetFloor());
                    newSizeOfPassengerRequest = newSizeOfPassengerRequest - hasSecondTakedPassengerPackage.getPassengerSize();

                } else if (newSizeOfPassengerRequest == hasSecondTakedPassengerPackage.getPassengerSize()) {
                    //Equals, this is enough
                    ElevatorData.appendPassenger(hasSecondTakedPassengerPackage.getPassengerSize(), hasSecondTakedPassengerPackage.getTargetFloor());
                    newSizeOfPassengerRequest = 0;
                }
                System.out.println("Zemin kattan Asansöre alınan yolcu sayısı:"+sizeOfPassengerRequest );

            }
        } else if (hasTakedPassengerPackage.getPassengerSize() == sizeOfPassengerRequest) {
            //Make full elevator
            //System.out.println("Koşul-3");
            ElevatorData.appendPassenger(sizeOfPassengerRequest, hasTakedPassengerPackage.getTargetFloor());
            System.out.println("Zemin kattan Asansöre alınan yolcu sayısı:"+sizeOfPassengerRequest );
        }

        return ElevatorData;
    }

    //Methods for Elevator Thread (Login-bring(getoff) passengers floor1,2,3,4 from Elevator) [Elevator --> floor1,2,3,4]
    public void bringPassengersFromElevator_to_floor1(elevatorData ElevatorData) {
        //Checking floor1 for want to getoff passengers
        if (ElevatorData.getTargetGoalFloor1_Size() > 0) {
            floor1 += ElevatorData.getTargetGoalFloor1_Size();
            ElevatorData.setTargetGoalFloor1_Size(0);
        }

    }

    public void bringPassengersFromElevator_to_floor2(elevatorData ElevatorData) {
        //Checking floor2 for want to getoff passengers
        if (ElevatorData.getTargetGoalFloor2_Size() > 0) {
            floor2 += ElevatorData.getTargetGoalFloor2_Size();
            ElevatorData.setTargetGoalFloor2_Size(0);
        }

    }

    public void bringPassengersFromElevator_to_floor3(elevatorData ElevatorData) {
        //Checking floor3 for want to getoff passengers
        if (ElevatorData.getTargetGoalFloor3_Size() > 0) {
            floor3 += ElevatorData.getTargetGoalFloor3_Size();
            ElevatorData.setTargetGoalFloor3_Size(0);
        }

    }

    public void bringPassengersFromElevator_to_floor4(elevatorData ElevatorData) {
        //Checking floor4 for want to getoff passengers
        if (ElevatorData.getTargetGoalFloor4_Size() > 0) {
            floor4 += ElevatorData.getTargetGoalFloor4_Size();
            ElevatorData.setTargetGoalFloor4_Size(0);
        }

    }

    //Methods for Elevator Thread (Exit-take passengers que1,2,3,4 to Elevator.)[Que1,2,3,4 --> Elevator]
    public elevatorData TakePassengersFromQue1_to_Elevator_Check(int sizeOfPassengerRequest) throws InterruptedException {
        //Checking Que1 for go to exit.
        passengerInfo hasTakedPassengerPackage = floorQue1_Consume();
        elevatorData ElevatorData = new elevatorData();
        if (sizeOfPassengerRequest < hasTakedPassengerPackage.getPassengerSize()) {
            //Too much passenger for elevator. (Set elevator full, send back liftOver passenger to Que)

            //1) make full elevator
            ElevatorData.appendPassenger(sizeOfPassengerRequest, hasTakedPassengerPackage.getTargetFloor());
            //2) send back liftOver passenger to Que
            int leftOverPassengerSize = hasTakedPassengerPackage.getPassengerSize() - sizeOfPassengerRequest;
            passengerInfo leftOverPassenger = new passengerInfo(hasTakedPassengerPackage.getTargetFloor(), leftOverPassengerSize);
            floorQue1_Produce(leftOverPassenger);

        } else if (sizeOfPassengerRequest > hasTakedPassengerPackage.getPassengerSize()) {
            //Not enough for full the elevator. Take other passengerPackage.

            //1)Append the passengers
            ElevatorData.appendPassenger(hasTakedPassengerPackage.getPassengerSize(), hasTakedPassengerPackage.getTargetFloor());
            //2)take package until will be enough
            int newSizeOfPassengerRequest = sizeOfPassengerRequest - hasTakedPassengerPackage.getPassengerSize();
            while (newSizeOfPassengerRequest == 0) {

                passengerInfo hasSecondTakedPassengerPackage = floorQue1_Consume();
                if (newSizeOfPassengerRequest < hasSecondTakedPassengerPackage.getPassengerSize()) {
                    //Too much passengers. Elevator is full, resend leftOver passengers package.

                    //1)do elevator full
                    ElevatorData.appendPassenger(newSizeOfPassengerRequest, hasSecondTakedPassengerPackage.getTargetFloor());
                    newSizeOfPassengerRequest = 0;

                    //2)resend passenger package
                    int leftOverPassengerSize = hasSecondTakedPassengerPackage.getPassengerSize() - newSizeOfPassengerRequest;
                    passengerInfo leftOverPassenger = new passengerInfo(hasSecondTakedPassengerPackage.getTargetFloor(), leftOverPassengerSize);
                    floorQue1_Produce(leftOverPassenger);

                } else if (newSizeOfPassengerRequest > hasSecondTakedPassengerPackage.getPassengerSize()) {
                    //Not enough passengers. take other packages.
                    ElevatorData.appendPassenger(hasSecondTakedPassengerPackage.getPassengerSize(), hasSecondTakedPassengerPackage.getTargetFloor());
                    newSizeOfPassengerRequest = newSizeOfPassengerRequest - hasSecondTakedPassengerPackage.getPassengerSize();

                } else if (newSizeOfPassengerRequest == hasSecondTakedPassengerPackage.getPassengerSize()) {
                    //Equals, this is enough
                    ElevatorData.appendPassenger(hasSecondTakedPassengerPackage.getPassengerSize(), hasSecondTakedPassengerPackage.getTargetFloor());
                    newSizeOfPassengerRequest = 0;
                }

            }

        } else if (sizeOfPassengerRequest == hasTakedPassengerPackage.getPassengerSize()) {
            ElevatorData.appendPassenger(sizeOfPassengerRequest, hasTakedPassengerPackage.getTargetFloor());
        }

        return ElevatorData;
    }

    //New Solve Multiple Methods for Eleveator thread (Exit (Que1,2,3,4 --> Elevator))
    public passengerInfo startingConsumeForElevator(int floorNumber) throws InterruptedException {
        System.out.println("Consume deyiz, FloorNumber:" + floorNumber);
        passengerInfo hasTakedPassengerPackage = null;
        if (floorNumber == 0) {
            System.out.println("0.kat consume edildi");
            hasTakedPassengerPackage = floorQue0_Consume();
        }
        if (floorNumber == 1) {
            System.out.println("1.kat consume edildi");
            hasTakedPassengerPackage = floorQue1_Consume();
        }
        if (floorNumber == 2) {
            System.out.println("2.kat consume edildi");
            hasTakedPassengerPackage = floorQue2_Consume();
        }
        if (floorNumber == 3) {
            System.out.println("3.kat consume edildi");
            hasTakedPassengerPackage = floorQue3_Consume();
        }
        if (floorNumber == 4) {
            System.out.println("4.kat consume edildi");
            hasTakedPassengerPackage = floorQue4_Consume();
        }
        if (hasTakedPassengerPackage != null) {
            System.out.println("PackagePassngerSize:" + hasTakedPassengerPackage.getPassengerSize() + " Floor:" + hasTakedPassengerPackage.getTargetFloor());
        } else {
            System.out.println("!!!!!!!!---NULL---!!!!!!!!!");
        }

        return hasTakedPassengerPackage;
    }

    public void multipleProduceForElevator_leftOverPassenger(passengerInfo leftOverPassenger, int floorNumber) {
        if (floorNumber == 0) {
            floorQue0_Produce(leftOverPassenger);
        }
        if (floorNumber == 1) {
            floorQue1_Produce(leftOverPassenger);
        }
        if (floorNumber == 2) {
            floorQue2_Produce(leftOverPassenger);
        }
        if (floorNumber == 3) {
            floorQue3_Produce(leftOverPassenger);
        }
        if (floorNumber == 4) {
            floorQue4_Produce(leftOverPassenger);
        }
    }

    public elevatorData TakePassengersFromQue0_1_2_3_4_to_Elevator_Check(int sizeOfPassengerRequest, int floorNumber) throws InterruptedException {
        //Checking Que1 for go to exit.
        passengerInfo hasTakedPassengerPackage = startingConsumeForElevator(floorNumber);
        System.out.println("|||||||||||||||||||||QUE-->ELEVATOR floor Number :" + floorNumber);
        elevatorData ElevatorData = new elevatorData();
        if (sizeOfPassengerRequest < hasTakedPassengerPackage.getPassengerSize()) {
            System.out.println("Que-->Elevator   Koşul-1");
            //Too much passenger for elevator. (Set elevator full, send back liftOver passenger to Que)

            //1) make full elevator
            ElevatorData.appendPassenger(sizeOfPassengerRequest, hasTakedPassengerPackage.getTargetFloor());
            //2) send back liftOver passenger to Que
            int leftOverPassengerSize = hasTakedPassengerPackage.getPassengerSize() - sizeOfPassengerRequest;
            passengerInfo leftOverPassenger = new passengerInfo(hasTakedPassengerPackage.getTargetFloor(), leftOverPassengerSize);
            multipleProduceForElevator_leftOverPassenger(leftOverPassenger, floorNumber);
            floorQue1_Produce(leftOverPassenger);

        } else if (sizeOfPassengerRequest > hasTakedPassengerPackage.getPassengerSize()) {
            System.out.println("\n~~~~~~Que-->Elevator   Koşul-2\n");
            //Not enough for full the elevator. Take other passengerPackage.
            System.out.println("RequestSize: " + sizeOfPassengerRequest + " Package Passenger Size: " + hasTakedPassengerPackage.getPassengerSize());
            //1)Append the passengers
            ElevatorData.appendPassenger(hasTakedPassengerPackage.getPassengerSize(), hasTakedPassengerPackage.getTargetFloor());

            //2)take package until will be enough
            int newSizeOfPassengerRequest = sizeOfPassengerRequest - hasTakedPassengerPackage.getPassengerSize();

            if (newSizeOfPassengerRequest > 0) {
                passengerInfo hasSecondTakedPassengerPackage = startingConsumeForElevator(floorNumber);

                while (newSizeOfPassengerRequest > 0) {
                    System.out.println("~~~~~~~~~~While Döngüsündeyiz");

                    //passengerInfo hasSecondTakedPassengerPackage = floorQue1_Consume();
                    if (newSizeOfPassengerRequest < hasSecondTakedPassengerPackage.getPassengerSize()) {
                        //kalan yer eldeki paketten az ise (kalan yer kadar adam al, artan adamı paketle iade et)
                        System.out.println("Que-->Elevator   Koşul-2.1");
                        //Too much passengers. Elevator is full, resend leftOver passengers package.

                        //1)do elevator full
                        ElevatorData.appendPassenger(newSizeOfPassengerRequest, hasSecondTakedPassengerPackage.getTargetFloor());
                        newSizeOfPassengerRequest = 0;

                        //2)resend passenger package
                        int leftOverPassengerSize = hasSecondTakedPassengerPackage.getPassengerSize() - newSizeOfPassengerRequest;
                        passengerInfo leftOverPassenger = new passengerInfo(hasSecondTakedPassengerPackage.getTargetFloor(), leftOverPassengerSize);
                        multipleProduceForElevator_leftOverPassenger(leftOverPassenger, floorNumber);
                        //floorQue1_Produce(leftOverPassenger);

                    } else if (newSizeOfPassengerRequest > hasSecondTakedPassengerPackage.getPassengerSize()) {
                        System.out.println("Que-->Elevator   Koşul-2.2");
                        System.out.println("-->newSizeOfPassengerRequest:" + newSizeOfPassengerRequest + " will be Taked:" + hasSecondTakedPassengerPackage.getPassengerSize());
                        //Not enough passengers. take other packages.
                        ElevatorData.appendPassenger(hasSecondTakedPassengerPackage.getPassengerSize(), hasSecondTakedPassengerPackage.getTargetFloor());
                        System.out.println("passenger appended to ElevatorData ");

                        newSizeOfPassengerRequest = newSizeOfPassengerRequest - hasSecondTakedPassengerPackage.getPassengerSize();
                        System.out.println("-->newSizeOfPassengerRequest updated :" + newSizeOfPassengerRequest);

                        System.out.println("New Consume");
                        hasSecondTakedPassengerPackage = startingConsumeForElevator(floorNumber);
                        System.out.println("Koşul-2.2 son");
                        newSizeOfPassengerRequest = 0;

                    } else if (newSizeOfPassengerRequest == hasSecondTakedPassengerPackage.getPassengerSize()) {
                        System.out.println("Que-->Elevator   Koşul-2.3");
                        //Equals, this is enough
                        ElevatorData.appendPassenger(hasSecondTakedPassengerPackage.getPassengerSize(), hasSecondTakedPassengerPackage.getTargetFloor());
                        newSizeOfPassengerRequest = 0;
                    }

                }
            }
        } else if (sizeOfPassengerRequest == hasTakedPassengerPackage.getPassengerSize()) {
            System.out.println("Que-->Elevator   Koşul-3");
            ElevatorData.appendPassenger(sizeOfPassengerRequest, hasTakedPassengerPackage.getTargetFloor());
        }

        return ElevatorData;
    }

    public boolean isHasAnyElementInQue(int floorNumber) {

        if (floorNumber == 0) {
            return floorQue0.size() > 0;
        }
        if (floorNumber == 1) {
            return floorQue1.size() > 0;
        }
        if (floorNumber == 2) {
            return floorQue2.size() > 0;
        }
        if (floorNumber == 3) {
            return floorQue3.size() > 0;
        }
        if (floorNumber == 4) {
            return floorQue4.size() > 0;
        }

        return false;
    }

    public elevatorData TakePassengersFromQue0_1_2_3_4_to_Elevator_Check_2(int sizeOfPassengerRequest, int floorNumber) throws InterruptedException {
        elevatorData ElevatorData = new elevatorData();
        passengerInfo hasTakedPassengerPackage = null;

        if (isHasAnyElementInQue(floorNumber)) {
            hasTakedPassengerPackage = startingConsumeForElevator(floorNumber);
        }

        if (hasTakedPassengerPackage != null) {
            if (sizeOfPassengerRequest >= hasTakedPassengerPackage.getPassengerSize()) {
                System.out.println("\n~~~~~~Que-->Elevator   Koşul-1\n");
                //Not enough for full the elevator. Take other passengerPackage.
                System.out.println("RequestSize: " + sizeOfPassengerRequest + " Package Passenger Size: " + hasTakedPassengerPackage.getPassengerSize());
                //1)Append the passengers
                ElevatorData.appendPassenger(hasTakedPassengerPackage.getPassengerSize(), hasTakedPassengerPackage.getTargetFloor());

                //2)take package until will be enough
                //int newSizeOfPassengerRequest = sizeOfPassengerRequest - hasTakedPassengerPackage.getPassengerSize();
            } else {
                System.out.println("Que-->Elevator   Koşul-2");
                //Too much passenger for elevator. (Set elevator full, send back liftOver passenger to Que)

                //1) make full elevator
                ElevatorData.appendPassenger(sizeOfPassengerRequest, hasTakedPassengerPackage.getTargetFloor());
                //2) send back liftOver passenger to Que
                int leftOverPassengerSize = hasTakedPassengerPackage.getPassengerSize() - sizeOfPassengerRequest;
                passengerInfo leftOverPassenger = new passengerInfo(hasTakedPassengerPackage.getTargetFloor(), leftOverPassengerSize);
                multipleProduceForElevator_leftOverPassenger(leftOverPassenger, floorNumber);
                floorQue1_Produce(leftOverPassenger);
            }
        }

        return ElevatorData;
    }

    //Methods for Elevator Thread (Exit-bring passengers Que0[delete] form elevator)[Elevator --> Que0(Delete)]
    public elevatorData finalExitPassengers(elevatorData ElevatorData) {
        //Elevator --> delete

        //if elevator is in down_turn_floor0 reset(clear) to elevator. 
        totalPassedPassengerSize[0] += ElevatorData.getTargetGoalFloor0_Size();
        totalPassedPassengerSize[1] += ElevatorData.getTargetGoalFloor1_Size();
        totalPassedPassengerSize[2] += ElevatorData.getTargetGoalFloor2_Size();
        totalPassedPassengerSize[3] += ElevatorData.getTargetGoalFloor3_Size();
        totalPassedPassengerSize[4] += ElevatorData.getTargetGoalFloor4_Size();

        ElevatorData.setTargetGoalFloor0_Size(0);
        ElevatorData.setTargetGoalFloor1_Size(0);
        ElevatorData.setTargetGoalFloor2_Size(0);
        ElevatorData.setTargetGoalFloor3_Size(0);
        ElevatorData.setTargetGoalFloor4_Size(0);

        return ElevatorData;
    }

    public int getFinalExitPassengerSize() {
        int totalFinalExitSize = 0;
        for (int i = 0; i < totalPassedPassengerSize.length; i++) {
            totalFinalExitSize += totalPassedPassengerSize[i];
        }
        return totalFinalExitSize;
    }

    public int floorQue0_TotalPassengerSize() {
        int floorQue0_TotalPassengerSize = 0;
        for (passengerInfo passenger : floorQue0) {
            floorQue0_TotalPassengerSize += passenger.getPassengerSize();
        }
        return floorQue0_TotalPassengerSize;
    }

    public int floorQue1_TotalPassengerSize() {
        int floorQue1_TotalPassengerSize = 0;
        for (passengerInfo passenger : floorQue1) {
            floorQue1_TotalPassengerSize += passenger.getPassengerSize();
        }
        return floorQue1_TotalPassengerSize;
    }

    public int floorQue2_TotalPassengerSize() {
        int floorQue2_TotalPassengerSize = 0;
        for (passengerInfo passenger : floorQue2) {
            floorQue2_TotalPassengerSize += passenger.getPassengerSize();
        }
        return floorQue2_TotalPassengerSize;
    }

    public int floorQue3_TotalPassengerSize() {
        int floorQue3_TotalPassengerSize = 0;
        for (passengerInfo passenger : floorQue3) {
            floorQue3_TotalPassengerSize += passenger.getPassengerSize();
        }
        return floorQue3_TotalPassengerSize;
    }

    public int floorQue4_TotalPassengerSize() {
        int floorQue4_TotalPassengerSize = 0;
        for (passengerInfo passenger : floorQue4) {
            floorQue4_TotalPassengerSize += passenger.getPassengerSize();
        }
        return floorQue4_TotalPassengerSize;
    }

    public int totalFloorQuePassengerSize() {
        return floorQue0_TotalPassengerSize() + floorQue1_TotalPassengerSize() + floorQue2_TotalPassengerSize() + floorQue3_TotalPassengerSize() + floorQue4_TotalPassengerSize();
    }

    public int totalFloorPassengerSize() {
        return floor0 + floor1 + floor2 + floor3 + floor4;
    }

    //Methods For Exit Thread
    public void movePassengersFloorToExitQue(int floorNumber, int passengerRequest) {
        //Floor 0 ???
        if (passengerRequest != 0) {
            if (floorNumber == 1) {
                if (passengerRequest < floor1) {
                    floor1 = floor1 - passengerRequest;
                    passengerInfo passenger = new passengerInfo(0, passengerRequest);
                    floorQue1_Produce(passenger);
                } else if (passengerRequest >= floor1 && (floor1 != 0)) {
                    passengerInfo passenger = new passengerInfo(0, floor1);
                    floorQue1_Produce(passenger);
                    floor1 = 0;
                }
            }
            if (floorNumber == 2) {
                if (passengerRequest < floor2) {
                    floor2 = floor2 - passengerRequest;
                    passengerInfo passenger = new passengerInfo(0, passengerRequest);
                    floorQue2_Produce(passenger);
                } else if (passengerRequest >= floor2 && (floor2 != 0)) {
                    passengerInfo passenger = new passengerInfo(0, floor2);
                    floorQue2_Produce(passenger);
                    floor2 = 0;
                }
            }
            if (floorNumber == 3) {
                if (passengerRequest < floor3) {
                    floor3 = floor3 - passengerRequest;
                    passengerInfo passenger = new passengerInfo(0, passengerRequest);
                    floorQue3_Produce(passenger);
                } else if (passengerRequest >= floor3 && (floor3 != 0)) {
                    passengerInfo passenger = new passengerInfo(0, floor3);
                    floorQue3_Produce(passenger);
                    floor3 = 0;
                }
            }
            if (floorNumber == 4) {
                if (passengerRequest < floor4) {
                    floor4 = floor4 - passengerRequest;
                    passengerInfo passenger = new passengerInfo(0, passengerRequest);
                    floorQue4_Produce(passenger);
                } else if (passengerRequest >= floor4 && (floor4 != 0)) {
                    passengerInfo passenger = new passengerInfo(0, floor4);
                    floorQue4_Produce(passenger);
                    floor4 = 0;
                }
            }

        }

    }

}

class passengerInfo {

    private int targetFloor;
    private int passengerSize;

    public passengerInfo(int targetFloor, int passengerSize) {
        this.targetFloor = targetFloor;
        this.passengerSize = passengerSize;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        if (0 <= targetFloor && targetFloor < 5) {
            this.targetFloor = targetFloor;
        }
    }

    public int getPassengerSize() {
        return passengerSize;
    }

    public void setPassengerSize(int passengerSize) {
        if (0 <= passengerSize && passengerSize <= 10) {
            this.passengerSize = passengerSize;
        }
    }

}

class elevatorData {

    private int targetGoalFloor1_Size = 0;
    private int targetGoalFloor2_Size = 0;
    private int targetGoalFloor3_Size = 0;
    private int targetGoalFloor4_Size = 0;

    private int targetGoalFloor0_Size = 0; //will delete passenger

    private int currentTotalSize = 0;
    public final static int ELEVATOR_PASSANGER_CAPACITY = 10;

    private int currentFloorNumber = 0;

    public elevatorData() {
    }

    public void appendPassenger(int passengerSize, int targetFloor) {
        if (targetFloor == 0) {
            this.setTargetGoalFloor0_Size(this.getTargetGoalFloor0_Size() + passengerSize);
        }
        if (targetFloor == 1) {
            this.setTargetGoalFloor1_Size(this.getTargetGoalFloor1_Size() + passengerSize);
        }
        if (targetFloor == 2) {
            this.setTargetGoalFloor2_Size(this.getTargetGoalFloor2_Size() + passengerSize);
        }
        if (targetFloor == 3) {
            this.setTargetGoalFloor3_Size(this.getTargetGoalFloor3_Size() + passengerSize);
        }
        if (targetFloor == 4) {
            this.setTargetGoalFloor4_Size(this.getTargetGoalFloor4_Size() + passengerSize);
        }
    }

    public int getEmptyPassengerZoneInElevator() {
        return ELEVATOR_PASSANGER_CAPACITY - this.currentTotalSize;
    }

    public void updateCurrentTotalSize() {
        int newSize = getTargetGoalFloor1_Size() + getTargetGoalFloor2_Size() + getTargetGoalFloor3_Size() + getTargetGoalFloor4_Size() + getTargetGoalFloor0_Size();
        if (newSize <= 10) {
            this.currentTotalSize = newSize;
        } else {
            System.out.println("Elevator Passenger Size is Full");
        }
    }

    public int getTargetGoalFloor1_Size() {
        return targetGoalFloor1_Size;
    }

    public void setTargetGoalFloor1_Size(int targetGoalFloor1_Size) {
        boolean isInRangeIfSum = (this.currentTotalSize + targetGoalFloor1_Size) <= 10;
        if (isInRangeIfSum) {
            this.targetGoalFloor1_Size = targetGoalFloor1_Size;
            updateCurrentTotalSize();
        } else {
            System.out.println("Elevator Passenger Size is Full");
        }
    }

    public int getTargetGoalFloor2_Size() {
        return targetGoalFloor2_Size;
    }

    public void setTargetGoalFloor2_Size(int targetGoalFloor2_Size) {
        boolean isInRangeIfSum = (this.currentTotalSize + targetGoalFloor2_Size) <= 10;
        if (isInRangeIfSum) {
            this.targetGoalFloor2_Size = targetGoalFloor2_Size;
            updateCurrentTotalSize();
        } else {
            System.out.println("Elevator Passenger Size is Full");
        }
    }

    public int getTargetGoalFloor3_Size() {
        return targetGoalFloor3_Size;
    }

    public void setTargetGoalFloor3_Size(int targetGoalFloor3_Size) {
        boolean isInRangeIfSum = (this.currentTotalSize + targetGoalFloor3_Size) <= 10;
        if (isInRangeIfSum) {
            this.targetGoalFloor3_Size = targetGoalFloor3_Size;
            updateCurrentTotalSize();
        } else {
            System.out.println("Elevator Passenger Size is Full");
        }
    }

    public int getTargetGoalFloor4_Size() {
        return targetGoalFloor4_Size;
    }

    public void setTargetGoalFloor4_Size(int targetGoalFloor4_Size) {
        boolean isInRangeIfSum = (this.currentTotalSize + targetGoalFloor4_Size) <= 10;
        if (isInRangeIfSum) {
            this.targetGoalFloor4_Size = targetGoalFloor4_Size;
            updateCurrentTotalSize();
        } else {
            System.out.println("Elevator Passenger Size is Full");
        }
    }

    public int getCurrentTotalSize() {
        return currentTotalSize;
    }

    public int getTargetGoalFloor0_Size() {
        return targetGoalFloor0_Size;
    }

    public void setTargetGoalFloor0_Size(int targetGoalFloor0_Size) {

        boolean isInRangeIfSum = (this.currentTotalSize + targetGoalFloor4_Size) <= 10;
        if (isInRangeIfSum) {
            this.targetGoalFloor0_Size = targetGoalFloor0_Size;
            updateCurrentTotalSize();
        } else {
            System.out.println("Elevator Passenger Size is Full");
        }

    }

    public int getCurrentFloorNumber() {
        return currentFloorNumber;
    }

    public void setCurrentFloorNumber(int currentFloorNumber) {
        boolean isRange = (0 <= currentFloorNumber) && (currentFloorNumber <= 4);
        if (isRange) {
            this.currentFloorNumber = currentFloorNumber;
        } else {
            System.out.println("Floor Number is out of range");
        }
    }

}
