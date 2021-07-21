/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tekdersproje;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ertuğrul Demir
 */
public class Threads {

}

class LoginThread extends Thread {

    dataClass data;
    Random rand;
    passengerInfo CurrentPassenger;
    private int totalPassengerCount = 0;

    public LoginThread(dataClass data) {
        this.data = data;
    }

    @Override
    public void run() {
        while (true) {

            try {
                //passengerInfo passenger = createPassenger();
                CurrentPassenger = createPassenger();
                Thread.sleep(500);
                totalPassengerCount = +CurrentPassenger.getPassengerSize();
                //System.out.println("Eleman Oluşturuldu:\n"+"Size: "+CurrentPassenger.getPassengerSize()+" Target Floor:"+CurrentPassenger.getTargetFloor());
                data.floorQue0_Produce(CurrentPassenger);
                /*
                //print table starts here
                System.out.println("Elemanlar");
                for (passengerInfo passenger_tmp : data.floorQue0) {
                    System.out.println("Hedef kat: " + passenger_tmp.getTargetFloor() + ", Eleman Sayısı: " + passenger_tmp.getPassengerSize());
                }
                System.out.println("Elemanlar bitti\n\n");
                //print table ends here
                 */
            } catch (InterruptedException ex) {
                Logger.getLogger(LoginThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public passengerInfo createPassenger() {
        rand = new Random();

        int passengerSize = rand.nextInt(10) + 1;
        int targetFloor = rand.nextInt(4) + 1;

        passengerInfo passenger = new passengerInfo(targetFloor, passengerSize);

        return passenger;
    }

    public int getTotalPassengerCount() {
        return totalPassengerCount;
    }

    public void setTotalPassengerCount(int totalPassengerCount) {
        this.totalPassengerCount = totalPassengerCount;
    }

    public dataClass getData() {
        return data;
    }

    public void setData(dataClass data) {
        this.data = data;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public passengerInfo getCurrentPassenger() {
        return CurrentPassenger;
    }

    public void setCurrentPassenger(passengerInfo CurrentPassenger) {
        this.CurrentPassenger = CurrentPassenger;
    }

}

class ExitThread extends LoginThread {

    passengerInfo CurrentPassenger;
    private int totalPassengerCount;

    public ExitThread(dataClass data) {
        super(data);
    }

    @Override
    public void run() {

        while (true) {

            try {

                rand = new Random();
                int randomFloor = rand.nextInt(4) + 1;
                Thread.sleep(1000);

                this.CurrentPassenger = createPassenger();
                this.totalPassengerCount += this.CurrentPassenger.getPassengerSize();
                data.movePassengersFloorToExitQue(randomFloor, CurrentPassenger.getPassengerSize());
                //data.floorQue0_Produce(passenger);
                //En son --> Floor to FloorQue   (Take(Tam çek) and Put)  {Tan çekmek için floor blockQueleri Ayrı şekilde tanımlayabilriz..}

            } catch (InterruptedException ex) {
                Logger.getLogger(ExitThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @Override
    public passengerInfo createPassenger() {
        rand = new Random();

        int passengerSize = rand.nextInt(5) + 1;
        int targetFloor = 0;
        //int targetFloor = rand.nextInt(4) + 1;

        passengerInfo passenger = new passengerInfo(targetFloor, passengerSize);

        return passenger;
    }

    public passengerInfo getCurrentPassenger() {
        return CurrentPassenger;
    }

    public void setCurrentPassenger(passengerInfo CurrentPassenger) {
        this.CurrentPassenger = CurrentPassenger;
    }

    public int getTotalPassengerCount() {
        return totalPassengerCount;
    }

    public void setTotalPassengerCount(int totalPassengerCount) {
        this.totalPassengerCount = totalPassengerCount;
    }

}

class ElevatorThread extends Thread {

    boolean isActive = false;
    elevatorData ElevatorData = new elevatorData();
    dataClass Data;

    public ElevatorThread(dataClass Data) {
        this.Data = Data;
    }

    @Override
    public void run() {
        //isActive
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(ElevatorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (this.isActive) {
                //System.out.println("\n\n!!!!!!!Elevator Active!!!!!!!\n\n");
                try {
                    moveUp();
                    //this.ElevatorData.setCurrentFloorNumber(0);
                    moveDown();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ElevatorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public void action() throws InterruptedException {
        moveUp();
        this.ElevatorData.setCurrentFloorNumber(0);
        //moveDown();
    }

    public void stopAction() {
        boolean check = true;
        while (check) {
            if (isActive) {
                check = false;
            }
        }

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public int maxNumberOfFlat() {
        if (ElevatorData.getTargetGoalFloor4_Size() > 0) {
            return 4;
        } else if (ElevatorData.getTargetGoalFloor3_Size() > 0) {
            return 3;
        } else if (ElevatorData.getTargetGoalFloor2_Size() > 0) {
            return 2;
        } else if (ElevatorData.getTargetGoalFloor1_Size() > 0) {
            return 1;
        }
        return 0;
    }

    public int updateAvaiblePassengerZoneInElevator() {
        return this.ElevatorData.ELEVATOR_PASSANGER_CAPACITY - this.ElevatorData.getCurrentTotalSize();
    }

    public void moveUp() throws InterruptedException {
        System.out.println("\n\nAsansör Yukarı Yöne Hareketleniyor....\n\n");
        //Algorithm
        //1)Take all passengers from Que0
        //2)Check maximum number of floor
        //3)go up and check each floor

        //System.out.println("Şu anda moveUP dayız");
        //0  --> Que0 to Elevator
        this.ElevatorData.setCurrentFloorNumber(0);
        int emptyPassengerZoneNumber = updateAvaiblePassengerZoneInElevator();
        Thread.sleep(1000);
        //System.out.println(emptyPassengerZoneNumber + "  adet request");
        this.ElevatorData = this.Data.TakePassengersFromQue0_to_Elevator(emptyPassengerZoneNumber);
        
        //System.out.println("Eldeki:\n" + "Size: " + this.ElevatorData.getCurrentTotalSize());

        //Upper limit 
        int maxFlatNumber = maxNumberOfFlat();

        //1 Check for Floor1  [Elevator --> Floor1]  get off passengers if there is passengers which want to gettoff
        if (1 <= maxFlatNumber) {
            System.out.println("Asansör yukarı Çıkıyor...");
            //System.out.println("Kat 1 ");
            Thread.sleep(1000);
            Data.bringPassengersFromElevator_to_floor1(ElevatorData);
            ElevatorData.setCurrentFloorNumber(1);
            System.out.println("Kat-1");
        }

        //2 Check for Floor2  [Elevator --> Floor2]
        if (2 <= maxFlatNumber) {
            System.out.println("Asansör yukarı Çıkıyor...");
            //System.out.println("Kat 2 ");
            Thread.sleep(1000);
            Data.bringPassengersFromElevator_to_floor2(ElevatorData);
            ElevatorData.setCurrentFloorNumber(2);
            System.out.println("Kat-2");
        }

        //3 Check for Floor3  [Elevator --> Floor3]
        if (3 <= maxFlatNumber) {
            System.out.println("Asansör yukarı Çıkıyor...");
            //System.out.println("Kat 3 ");
            Thread.sleep(1000);
            Data.bringPassengersFromElevator_to_floor3(ElevatorData);
            ElevatorData.setCurrentFloorNumber(3);
            System.out.println("Kat-3");
        }

        //4 Check for Floor4  [Elevator --> Floor4]
        if (4 <= maxFlatNumber) {
            System.out.println("Asansör yukarı Çıkıyor...");
            //System.out.println("Kat 4 ");
            Thread.sleep(1000);
            Data.bringPassengersFromElevator_to_floor4(ElevatorData);
            ElevatorData.setCurrentFloorNumber(4);
            System.out.println("Kat-4");
        }

        /*
        //1,2,3,4 --> Check and if there is passenger  bring the floor.
        int maxFlatNumber=maxNumberOfFlat();
        for (int i = 1; i <= maxFlatNumber; i++) {
            emptyPassengerZoneNumber = ElevatorData.ELEVATOR_PASSANGER_CAPACITY-ElevatorData.getCurrentTotalSize();
            ElevatorData=Data.TakePassengersFromQue0_1_2_3_4_to_Elevator_Check(emptyPassengerZoneNumber, i);
        }
        
         */
    }

    public void goFloor(int moveNumber) throws InterruptedException {
        for (int i = 0; i < moveNumber; i++) {
            Thread.sleep(1000);
        }

    }

    public int findMaxQueNumber() {
        //we assume start upside  and move to downside (for do this we find starting upside)
        //(if there is passenger it can be starting side.)
        if (Data.floorQue4.size() > 0) {
            return 4;
        } else if (Data.floorQue3.size() > 0) {
            return 3;
        } else if (Data.floorQue2.size() > 0) {
            return 2;
        } else if (Data.floorQue1.size() > 0) {
            return 1;
        }
        return 0;
    }

    public boolean isElevatorEmpty() {
        return ElevatorData.getCurrentTotalSize() == 0;
    }

    public void moveDown() throws InterruptedException {
        boolean isInOutOfFloor0 = ElevatorData.getCurrentFloorNumber() != 0;

        System.out.println("\n\nAsansör Aşağı Yöne Hareketleniyor....\n\n");
        //Koşulları kaldırabiliriz.
        if (ElevatorData.getCurrentTotalSize() < 10 && isInOutOfFloor0) {
            //Go to max Que
            int maxQueNumber = findMaxQueNumber();
            System.out.println("çıkış talebinin olduğu en yüksek başlangıç katı:" + maxQueNumber);
            int moveNumber = Math.abs(ElevatorData.getCurrentFloorNumber() - maxQueNumber);
            System.out.println("-->" + ElevatorData.getCurrentFloorNumber() + ".kattan ==>" + maxQueNumber + ".kata  " + moveNumber + "adet kat gecilecek");
            System.out.println("-->Asansor iniş için  başlangıç katına çıkıyor...");
            goFloor(moveNumber);
            System.out.println("-->Asansör iniş için başlangıç katına ulaştı.");
            ElevatorData.setCurrentFloorNumber(maxQueNumber);
            
            int emptyPassengerZoneNumber = updateAvaiblePassengerZoneInElevator();
            System.out.println("-->"+emptyPassengerZoneNumber + " adet boş yer var");
            for (int i = maxQueNumber; i >= 0; i--) {
                System.out.println("-->Bulunduğumuz kat: "+ i);
                //Base Floor --> Exit Passenger. [Elevator to Exit(Delete)]
                if (i == 0) {
                    System.out.println("-->Yolcular çıkartılıyor...");
                    Thread.sleep(1000);
                    ElevatorData = Data.finalExitPassengers(ElevatorData);
                    System.out.println("-->Totalde Çıkan eleman sayısı: "+Data.getFinalExitPassengerSize());
                    ElevatorData.setCurrentFloorNumber(0);
                    //System.out.println("-->MoveDowndan çıkıyoruz...");
                    break;
                }
                
                System.out.println("--> Asansöre, Kuyruktan çıkış yapmak isteyen yolcuları alıyoruz...");
                //Take Passenngers  [Que1,2,3,4 to Elevator]
                Thread.sleep(1000);
                if(Data.isHasAnyElementInQue(i)){
                    ElevatorData = Data.TakePassengersFromQue0_1_2_3_4_to_Elevator_Check_2(emptyPassengerZoneNumber, i);
                }
                
                System.out.println("-->Yolcular alındı.");
                ElevatorData.setCurrentFloorNumber(i);
                System.out.println("-->Sonraki kata iniliyor...");
            }
            /*
            //Check que 4 (old system --> speial code for each floor )
            if(4<=maxQueNumber){
                Thread.sleep(200);
                ElevatorData=Data.TakePassengersFromQue0_1_2_3_4_to_Elevator_Check(emptyPassengerZoneNumber, 4);
                ElevatorData.setCurrentFloorNumber(4);
            }
             */
        } else if (ElevatorData.getCurrentTotalSize() == 10) {
            System.out.println("~~Asansör Ağzına kadar dolu~~");

        }

    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public elevatorData getElevatorData() {
        return ElevatorData;
    }

    public void setElevatorData(elevatorData ElevatorData) {
        this.ElevatorData = ElevatorData;
    }

    public dataClass getData() {
        return Data;
    }

    public void setData(dataClass Data) {
        this.Data = Data;
    }

}

class ControlThread_2 extends Thread {

}

class ControlThread extends Thread {

    private dataClass Data;

    private LoginThread loginThread;
    private ExitThread exitThread;

    private ElevatorThread ElevatorThread_1;
    private ElevatorThread ElevatorThread_2;
    private ElevatorThread ElevatorThread_3;
    private ElevatorThread ElevatorThread_4;
    private ElevatorThread ElevatorThread_5;

    private boolean isActiveElevatorThred_1 = true;
    private boolean isActiveElevatorThred_2 = false;
    private boolean isActiveElevatorThred_3 = false;
    private boolean isActiveElevatorThred_4 = false;
    private boolean isActiveElevatorThred_5 = false;

    public ControlThread(dataClass Data) {
        this.Data = Data;
    }

    public ControlThread(dataClass Data, LoginThread loginThread, ExitThread exitThread, ElevatorThread ElevatorThread_1, ElevatorThread ElevatorThread_2, ElevatorThread ElevatorThread_3, ElevatorThread ElevatorThread_4, ElevatorThread ElevatorThread_5) {
        this.Data = Data;
        this.loginThread = loginThread;
        this.exitThread = exitThread;
        this.ElevatorThread_1 = ElevatorThread_1;
        this.ElevatorThread_2 = ElevatorThread_2;
        this.ElevatorThread_3 = ElevatorThread_3;
        this.ElevatorThread_4 = ElevatorThread_4;
        this.ElevatorThread_5 = ElevatorThread_5;
    }

    @Override
    public void run() {
        //super.run(); //To change body of generated methods, choose Tools | Templates.

        while (true) {
            controlAndManageNumberOfElevators();
        }

        /*
        loginThread = new LoginThread(Data);
        exitThread = new ExitThread(Data);
        
        //controller_thread=new Controller_thread(avm_data);

        ElevatorThread_1 = new ElevatorThread(Data);
        ElevatorThread_2 = new ElevatorThread(Data);
        ElevatorThread_3 = new ElevatorThread(Data);
        ElevatorThread_4 = new ElevatorThread(Data);
        ElevatorThread_5 = new ElevatorThread(Data);
        //controller_thread._controller_thread_add(asansor_thread_1, asansor_thread_2, asansor_thread_3, asansor_thread_4, asansor_thread_5);

        //Starting Elevators 
        ElevatorThread_1.setIsActive(isActiveElevatorThred_1);
        //ElevatorThread_2.setIsActive(isActiveElevatorThred_2);
        ElevatorThread_2.setIsActive(true);
        ElevatorThread_3.setIsActive(isActiveElevatorThred_3);
        ElevatorThread_4.setIsActive(isActiveElevatorThred_4);
        ElevatorThread_5.setIsActive(isActiveElevatorThred_5);

        loginThread.start();
        exitThread.start();

        ElevatorThread_1.start();
        ElevatorThread_2.start();
        //ElevatorThread_3.start();
        //ElevatorThread_4.start();
        //ElevatorThread_5.start();

        //controller_thread.start();
        
        try {
            /*
            System.out.println("~~~~TRY CATCH-start");
            loginThread.join();
            System.out.println("~~~~Login");
            exitThread.join();
            System.out.println("~~~~Exit");
            
            ElevatorThread_1.join();
            System.out.println("~~~~ET-1");
            ElevatorThread_2.join();
            System.out.println("~~~~ET-2");
            
            //ElevatorThread_3.join();
            //ElevatorThread_4.join();
            //ElevatorThread_5.join();
            System.out.println("~~~~TRY CATCH-mid");  
            //this.join();
            
            
            while(true){
                System.out.println("~~~~Kontorl Ediliyor");
                controlAndManageNumberOfElevators();
            }
            
            //controller_thread.join();
        } catch (InterruptedException ex) {
            System.out.println("~~~~TRY CATCH-ERROR");
            Logger.getLogger(ControlThread.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public int activeElevatorNumber() {
        int activeElevatorNumber = 0;

        if (isActiveElevatorThred_1) {
            activeElevatorNumber++;
        }
        if (isActiveElevatorThred_2) {
            activeElevatorNumber++;
        }
        if (isActiveElevatorThred_3) {
            activeElevatorNumber++;
        }
        if (isActiveElevatorThred_4) {
            activeElevatorNumber++;
        }
        if (isActiveElevatorThred_5) {
            activeElevatorNumber++;
        }

        return activeElevatorNumber;
    }

    public int totalPassengerSizeFromQuesAndFloorsAndElevators() {

        int totalPassengersInQue = Data.totalFloorQuePassengerSize();
        int totalPassengersInFloor = Data.totalFloorPassengerSize();
        int totalPassengersInElevators = 0;

        if (isActiveElevatorThred_1) {
            totalPassengersInElevators += ElevatorThread_1.getElevatorData().getCurrentTotalSize();
        }
        if (isActiveElevatorThred_2) {
            totalPassengersInElevators += ElevatorThread_2.getElevatorData().getCurrentTotalSize();
        }
        if (isActiveElevatorThred_3) {
            totalPassengersInElevators += ElevatorThread_3.getElevatorData().getCurrentTotalSize();
        }
        if (isActiveElevatorThred_4) {
            totalPassengersInElevators += ElevatorThread_4.getElevatorData().getCurrentTotalSize();
        }
        if (isActiveElevatorThred_5) {
            totalPassengersInElevators += ElevatorThread_5.getElevatorData().getCurrentTotalSize();
        }
        return totalPassengersInQue + totalPassengersInFloor + totalPassengersInElevators;
    }

    public boolean isNeedNewElevator() {
        //int totalPassengerSize= totalPassengerSizeFromQuesAndFloorsAndElevators();
        int totalPassengersInQue = Data.totalFloorQuePassengerSize();
        int currentCapacity = elevatorData.ELEVATOR_PASSANGER_CAPACITY * activeElevatorNumber();
        //System.out.println("~~~~TurnUP~~~~~~Total Passenger Size: "+ totalPassengersInQue+"Current Capacity: "+currentCapacity);
        // if --> if TotalQuePassenger Number > Elevators Capacityy + 20 ; Active New elevator 
        if (totalPassengersInQue >= currentCapacity + 20) {
            return true;
        }

        return false;
    }

    public boolean isNeedTurnOffElevator() {
        int totalPassengersInQue = Data.totalFloorQuePassengerSize();
        int currentCapacity = elevatorData.ELEVATOR_PASSANGER_CAPACITY * activeElevatorNumber();
        //System.out.println("~~~TurnOFF~~~~~Total Passenger Size: "+ totalPassengersInQue+"Current Capacity: "+currentCapacity);
        // if --> if TotalQuePassenger Number < Elevators Capacityy; Turn off a elevator.
        if (totalPassengersInQue < currentCapacity) {
            return true;
        }
        return false;
    }

    public void acitiveAnElevator() {
        String seperator = "------------------------------------";
        if (!ElevatorThread_2.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_2 Activated");
            System.out.println(seperator + "\n\n");
            isActiveElevatorThred_2 = true;
            //ElevatorThread_2.notify();
            ElevatorThread_2.isActive = true;
            //ElevatorThread_2.run();
        } else if (!ElevatorThread_3.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_3 Activated");
            System.out.println(seperator + "\n\n");
            isActiveElevatorThred_3 = true;
            //ElevatorThread_3.notify();
            ElevatorThread_3.isActive = true;
            //ElevatorThread_3.run();
        } else if (!ElevatorThread_4.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_4 Activated");
            System.out.println(seperator + "\n\n");
            isActiveElevatorThred_4 = true;
            //ElevatorThread_4.notify();
            ElevatorThread_4.isActive = true;
            //ElevatorThread_4.run();
        } else if (!ElevatorThread_5.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_5 Activated");
            System.out.println(seperator + "\n\n");
            isActiveElevatorThred_5 = true;
            //ElevatorThread_5.notify();
            ElevatorThread_5.isActive = true;
            //ElevatorThread_5.run();
        } else {
            //System.out.println("Aktifleştirecek Asansör Kalmadı.");
        }
    }

    public void pasiveAnElevator() throws InterruptedException {
        String seperator = "------------------------------------";
        if (ElevatorThread_5.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_5 Passived");
            System.out.println(seperator + "\n\n");
            isActiveElevatorThred_5 = false;
            //ElevatorThread_5.wait();
            ElevatorThread_5.isActive = false;
        } else if (ElevatorThread_4.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_4 Passived");
            System.out.println(seperator + "\n\n");
            isActiveElevatorThred_4 = false;
            //ElevatorThread_4.wait();
            ElevatorThread_4.isActive = false;
        } else if (ElevatorThread_3.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_3 Passived");
            System.out.println(seperator + "\n\n");
            isActiveElevatorThred_3 = false;
            //ElevatorThread_3.wait();
            ElevatorThread_3.isActive = false;
        } else if (ElevatorThread_2.isActive) {
            System.out.println("\n\n" + seperator);
            System.out.println("Elevator_2 Passived");
            System.out.println(seperator + "\n\n");
            ElevatorThread_2.wait();
            //isActiveElevatorThred_2 = false;
            ElevatorThread_2.isActive = false;
        }
    }

    public void controlAndManageNumberOfElevators() {

        if (isNeedNewElevator()) {
            //ystem.out.println("~~~~~~~~~~Yeni Bir Asansör Açılıyor...");
            acitiveAnElevator();
        }

        if (isNeedTurnOffElevator()) {
            try {
                //System.out.println("~~~~~~~~~~Bir adet Asansör Kapanıyor...");
                pasiveAnElevator();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public dataClass getData() {
        return Data;
    }

    public void setData(dataClass Data) {
        this.Data = Data;
    }

    public LoginThread getLoginThread() {
        return loginThread;
    }

    public void setLoginThread(LoginThread loginThread) {
        this.loginThread = loginThread;
    }

    public ExitThread getExitThread() {
        return exitThread;
    }

    public void setExitThread(ExitThread exitThread) {
        this.exitThread = exitThread;
    }

    public ElevatorThread getElevatorThread_1() {
        return ElevatorThread_1;
    }

    public void setElevatorThread_1(ElevatorThread ElevatorThread_1) {
        this.ElevatorThread_1 = ElevatorThread_1;
    }

    public ElevatorThread getElevatorThread_2() {
        return ElevatorThread_2;
    }

    public void setElevatorThread_2(ElevatorThread ElevatorThread_2) {
        this.ElevatorThread_2 = ElevatorThread_2;
    }

    public ElevatorThread getElevatorThread_3() {
        return ElevatorThread_3;
    }

    public void setElevatorThread_3(ElevatorThread ElevatorThread_3) {
        this.ElevatorThread_3 = ElevatorThread_3;
    }

    public ElevatorThread getElevatorThread_4() {
        return ElevatorThread_4;
    }

    public void setElevatorThread_4(ElevatorThread ElevatorThread_4) {
        this.ElevatorThread_4 = ElevatorThread_4;
    }

    public ElevatorThread getElevatorThread_5() {
        return ElevatorThread_5;
    }

    public void setElevatorThread_5(ElevatorThread ElevatorThread_5) {
        this.ElevatorThread_5 = ElevatorThread_5;
    }

    public boolean isIsActiveElevatorThred_1() {
        return isActiveElevatorThred_1;
    }

    public void setIsActiveElevatorThred_1(boolean isActiveElevatorThred_1) {
        this.isActiveElevatorThred_1 = isActiveElevatorThred_1;
    }

    public boolean isIsActiveElevatorThred_2() {
        return isActiveElevatorThred_2;
    }

    public void setIsActiveElevatorThred_2(boolean isActiveElevatorThred_2) {
        this.isActiveElevatorThred_2 = isActiveElevatorThred_2;
    }

    public boolean isIsActiveElevatorThred_3() {
        return isActiveElevatorThred_3;
    }

    public void setIsActiveElevatorThred_3(boolean isActiveElevatorThred_3) {
        this.isActiveElevatorThred_3 = isActiveElevatorThred_3;
    }

    public boolean isIsActiveElevatorThred_4() {
        return isActiveElevatorThred_4;
    }

    public void setIsActiveElevatorThred_4(boolean isActiveElevatorThred_4) {
        this.isActiveElevatorThred_4 = isActiveElevatorThred_4;
    }

    public boolean isIsActiveElevatorThred_5() {
        return isActiveElevatorThred_5;
    }

    public void setIsActiveElevatorThred_5(boolean isActiveElevatorThred_5) {
        this.isActiveElevatorThred_5 = isActiveElevatorThred_5;
    }

}
