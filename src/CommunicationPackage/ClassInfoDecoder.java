package CommunicationPackage;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import old.errorDevice.TableViewErrorClass;

import java.util.ArrayList;

public class ClassInfoDecoder {

    public int codeNumber[]={
            250,
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
            27,
            28,
            29,
            30,

    };
    public String variableInProgram[]={
            "UNIMPORTANT_ERROR",
            "HCSR04_INIT_ERROR",
            "UARTCOM_INIT_ERROR",
            "UARTCOM_ADDFRAME_FULL",
            "UARTCOM_ADDFRAME_INVALID_LENGTH_FRAME",
            "UARTCOM_ADDFRAME_NOT_EXECUTE_PREVIOUS_FUN",
            "ERROR_ERRORCODEPUSH_FULL",
            "VL5310X_INCORRECT_MODEL_ID",
            "VL5310X_INCORRECT_BUDGET_US",
            "VL5310X_INCORRECT_PERIOD_VCSELPERIODPRERANGE",
            "VL5310X_INCORRECT_PERIOD_VCSELPERIODFINALRANGE",
            "VL5310X_INCORRECT_TYPE_VCSELPERIODTYPE",
            "VL5310X_TIMEOUT_READRANGECONTINUOUSMILLIMETERS",
            "VL5310X_TIMEOUT_READRANGESINGLEMILLIMETERS",
            "VL5310X_TIMEOUT_GETSPADINFO",
            "VL5310X_ERROR_ENCODETIMEOUT",
            "VL5310X_TIMEOUT_PERFORMSINGLEREFCALIBRATION",
            "MAIN_HCSR04_GET_DISTANCE_TIMEOUT",
            "STATEMACHINE_TIMEOUT",
            "STATEMACHINE_GPIO_POINTER_NULL",
            "STATEMACHINE_HTIM_POINTER_NULL",
            "UARTCOM_HTIM_POINTER_NULL",
            "IRSENSOR_NUMBER_OUT_OF_SIZE",
            "IRSENSOR_GPIO_POINTER_NULL",
            "UARTCOM_MAX_LENGTH_IR_STATUS_SEND",
            "FLASH_BASE_STRUCT_NULL",
            "FLASH_UNLOCK_ERROR",
            "FLASH_ERASE_ERROR",
            "ADC_HADC_NULL_POINTER",
            "SERVO360_NO_NUMBER_IN_ARRAY",
            "SERVO360_CURRENTPOSITION_OUT_OF_RANGE",

    };
    public String source[]={
            "Dowolny",
            "hc-sr04",
            "uartCom",
            "uartCom addFrame()",
            "uartCom addFrame()",
            "uartCom addFrame()",
            "errorCode errorCodePush()",
            "vl5310x init()",
            "vl5310x setMeasurementTimingBudget()",
            "vc5310x setVcselPulsePeriod()",
            "vc5310x setVcselPulsePeriod()",
            "vc5310x setVcselPulsePeriod()",
            "vc5310x readRangeContinuousMillimeters()",
            "vc5310x readRangeSingleMillimeters()",
            "vc5310x getSpadInfo()",
            "vc5310x encodeTimeout()",
            "vl5310x performSingleRefCalibration()",
            "main Odczyt z HCSR04",
            "statemachine timeout licznika",
            "statemachine",
            "statemachine",
            "uartcom uartcomServoNextPoition() / uartComServoPreviousPosition()",
            "irSensor inirIrPinout()",
            "irSensor isSensorReadStatusIrSensor()",
            "uartCom uartComSendIrSensorStatus",
            "flash flashSerachSaveAndReadAddress()",
            "flash flasClearMemory()  flashWriteMemory()",
            "flash flasClearMemory()",
            "adc",
            "servo360 servo360SetCurrentPositionByPositionNumber()",
            "servo360",

    };
    public String comment[]={
            "Bład nieistotny (nie wymaga poprawy/uwagi)",
            "Nie zainicjalizowano wskażnika hssc04Tim2_p",
            "Nie zainicjalizowano wskażnika uartCommunication_p",
            "Bufor komend jest przepełniony",
            "Odebrano niepoprawny rozmiar ramki",
            "Nie wykonano wcześniejszej komendy",
            "Bufor jest przepełniony",
            "Odczytany numer identyfikacyjny jest niepoprawny",
            "Wartość budget_us jest mniejsza od minimalnej",
            "zmienna period_pclks nie znajduje się w podanym zakresie",
            "zmienna period_pclks nie znajduje się w podanym zakresie",
            "niepoprawny typ vcselPeriodType",
            "Timeout funkcji while",
            "Timeout funkcji while",
            "Timeout funkcji while",
            "Bład funkcji odczytujacej timeout z układu",
            "Timeout funkcji while",
            "Brak połączenie z czujnikiem ultradzwiekowym",
            "Zakończenie jazdy poprzez timeout licznika 17",
            "Nie został zainicjalizowany wskażnika gpio w struktórze  drivingStructure",
            "Nie został zainicjalizowany wskażnika htim w struktórze  drivingStructure",
            "Nie został zainicjalizowany wskażnika htim w struktórze  uartComSensorFrame",
            "wartośc number jest poza zakresem stałej MAX_SENSOR_IR",
            "Nie został zainicjalizowany wskażnika gpio w struktórze  irSensor",
            "Nie można wysłać wiekszego statusu czujników ponieważ musi mieścić się na 32 bitach",
            "Funkcja nie może znaleźć zmiennych baseAddress, lastAddress lub flashRowIndex",
            "wykruto makro HAL_ERROR HAL_FLASH_Unlock()",
            "wykruto makro HAL_ERROR w funkcji HAL_FLASHEx_Erase()",
            "Nie został zainicjalizowany wskażnika hadc w struktórze  adcBaseStructure",
            "numer pozycji wychodzi ponad zmienną SERVO360_MAX_POSITION",
            "zmienna currentPosition wychodzi poza dopuszczalny zakres",

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
