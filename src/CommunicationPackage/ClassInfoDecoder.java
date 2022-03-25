package CommunicationPackage;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import old.errorDevice.TableViewErrorClass;

import java.util.ArrayList;

public class ClassInfoDecoder {

    public int codeNumber[]={
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            26,


    };
    public String variableInProgram[]={
            "CommandDecoder_OutOfBuffer",
            "CommandDecoder_WaitToHandle",
            "CommandDecoder_Timeout_Decod",
            "VL5310X_IncorrectModelID",
            "VL5310X_IncorrectBudgetUs",
            "VL5310X_IncorrectPerodVcSelPeriodPrerange",
            "VL5310X_IncorrectPeriodVcSelPeriodFinalrange",
            "VL5310X_IncorrectTypeVcselperiodType",
            "VL53L0X_NullXShulPointer",
            "CAR_OutOdSizeFunctionSevices",
            "HCSR04_SatusNoIdle",
            "RANGEMEASURMENT_TimeoutPositionChangingFunction",
            "RANGEMEASURMENT_TimeoutMeasurmentDistanceFunction",
            "RANGEMEASURMENT_NoSetLockHcSr04",
            "RANGEMEASURMENT_NoSetLockVl53l0x",
            "SERVOPR_OutOfMaxServoPosition",
            "VL5310X_ErrorGetSpadInfo",
            "VL5310x_ErrorPerformSingleRefCalibration",
            "VL5310x_TimeoutGetSpadInfo",
            "VL5310X_OutOfTypeVcSelPeriodType",
            "VL5310X_ErrorEndCodeTimeout",
            "VL5310X_StatusNoIdle",
            "CAR_OutOfSizeStateMachine",
            "CAR_TimeoutResetMotorControl",
            "CAR_MeasureDistanceForPcCountIsNoZero",
            "CAR_SizeStateMachineIsZero",


    };
    public String source[]={
            "CommandDecoder",
            "CommandDecoder",
            "CommandDecoder",
            "vl5310x init()",
            "vl5310x setMeasurementTimingBudget()",
            "vc5310x setVcselPulsePeriod()",
            "vc5310x setVcselPulsePeriod()",
            "vc5310x setVcselPulsePeriod()",
            "vl53l0xInit()",
            "Car_mainFun()",
            "HcSr04_StartMeasurment()",
            "RangeMeasurment_PostitionChangingFunction()",
            "RangeMeasurment_MeasurmentDistanceFunction()",
            "RangeMeasurment_MeasurmentDistanceFunction()",
            "RangeMeasurment_MeasurmentDistanceFunction()",
            "servoPR_SetPosition()",
            "vl53l0x_SensorInit()",
            "vl53l0x_SensorInit()",
            "vl53l0x_GetSpadInfo()",
            "vl53l0x_GetVcselPulsePeriod()",
            "vl53l0x_EncodeTimeout()",
            "vl53l0x_StartSingleMeasurment()",
            "Car_NextPositionStateMachineControlCar()",
            "Car_mainFun()",
            "Car_ResetDriving()",
            "Car_PrevPositionStateMachineControlCar()",


    };
    public String comment[]={
            "Wyjście poza zakres bufora",
            "Oczekiwanie na obsłużenie funkcji która została wykonana",
            "Zbyt długie oczekiwanie na dostarczenie komendy",
            "Odczytany numer identyfikacyjny jest niepoprawny",
            "Wartość budget_us jest mniejsza od minimalnej",
            "zmienna period_pclks nie znajduje się w podanym zakresie",
            "zmienna period_pclks nie znajduje się w podanym zakresie",
            "niepoprawny typ vcselPeriodType",
            "Brak wzkażnika na pin odpowiadający za połączenie do XSHUT",
            "Funkcja dekodujaca zwróciła większą wartość niż ilość funkcji obsługi",
            "Nie można uruchomić pomiaru ponieważ poprzedni pomiar się nie zakończył",
            "Timeout przy zmianie pozycji serwomechanizmu",
            "Timeout przy odczycie odległości",
            "Nie ustawiono flagi hcSr04LockValue",
            "Nie ustawiono flagi Vl53l0xLockValue",
            "Chęć ustawienia pozycji serwomechanizmu spoza zakresu",
            "Bład podczas wykonywania funkcji",
            "Bład podczas wykonywania funkcji",
            "Bład podczas wykonywania funkcji",
            "Wybrano typ vcselPeriodType poza znaną wartość",
            "timeout odkodowanie",
            "Nie można uruchomić pomiaru ponieważ poprzedni pomiar się nie zakończył",
            "Ustawienie pozycji powyżej maksymalnej",
            "Timeout resetu układu jazdy",
            "Podczas resetu kierunku jazdy zmienna measureDistanceForPcCount była wieksza od 0",
            "Nie można zejść poniżej zera",

};

    public ClassInfo getDecodedClassInfo(ArrayList<Integer> data){
        ClassInfo classInfo=null;
        int i;
        for(i=0;i<codeNumber.length;i++){
            if(data.get(2) == codeNumber[i]){
                classInfo=new ClassInfo(codeNumber[i],variableInProgram[i],source[i],comment[i]);
                --i;
                break;
            }
        }
        if(i>=codeNumber.length){
            System.out.print("Nie rozpoznano błedu:\t");
            for(int x:data){
                System.out.print(String.format("%x",x));
            }
            System.out.println("");
        }
        return classInfo;
    }
    public ClassInfo getDecodeClassInfoStandard(){
        ClassInfo classInfo=new ClassInfo(codeNumber[0],variableInProgram[0],source[0],comment[0]);
        return classInfo;
    }
}
