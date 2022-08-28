import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Calculus {

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();
        String[] expression = input.split(" ");
        NumberType expressionType = checkExpressionType(expression);
        int[] numbers = getNumbers(expression, expressionType);
        String operator = expression[1];
        Integer result = calculate(numbers[0], numbers[1], operator);
        if (expressionType == NumberType.ROMAN && result < 1)
            throw new Exception("Cannot present roman negative number");
        String output = expressionType == NumberType.ROMAN ? convertIntToRoman(result) : result.toString();
        if (numbers[0] > 10 || numbers[1] > 10)
            throw new Exception("One(or more) of input numbers is bigger then 10 (calculation result is: " + output + ")");
        System.out.println(output);
    }

    private static int calculate(int firstNumber, int secondNumber, String operator) throws Exception{
        switch (operator){
            case ("+"):
                return firstNumber + secondNumber;
            case ("-"):
                return firstNumber - secondNumber;
            case ("*"):
                return firstNumber * secondNumber;
            case ("/"):
                return firstNumber / secondNumber;
            default:
                throw new Exception("Wrong operator");
        }
    }

    private static int[] getNumbers(String[] expression, NumberType expressionType){
        int firstNumber;
        int secondNumber;
        if (expressionType == NumberType.ARABIC){
            firstNumber = convertArabicToInt(expression[0]);
            secondNumber = convertArabicToInt(expression[2]);
        }
        else {
            firstNumber = convertRomanToInt(expression[0]);
            secondNumber = convertRomanToInt(expression[2]);
        }
        return new int[] {firstNumber, secondNumber};
    }



    //region Checkups
    private static NumberType checkExpressionType(String[] expression) throws Exception{
        if (expression.length != 3)
            throw new Exception("Invalid expression length (expected 3, got " + expression.length + ")");
        if (expression[1] == ""){
            throw new Exception("No operator found");
        }
        if (!isOperator(expression[1]))
            throw new Exception("Unknown operator");
        NumberType firstNumber = checkNumberType(expression[0]);
        NumberType secondNumber = checkNumberType(expression[2]);
        if (firstNumber == NumberType.UNKNOWN)
            throw new Exception("Invalid first number");
        if (secondNumber == NumberType.UNKNOWN)
            throw new Exception("Invalid second number");
        if (firstNumber != secondNumber)
            throw new Exception("Types of the first and second numbers do not match");
        if (firstNumber == NumberType.ARABIC)
            return NumberType.ARABIC;
        else
            return NumberType.ROMAN;

    }

    private static NumberType checkNumberType(String number){
        if (isArabic(number)){
            return NumberType.ARABIC;
        }
        else if (isRoman(number)){
            return NumberType.ROMAN;
        }
        else{
            return NumberType.UNKNOWN;
        }
    }
    private static boolean isArabic(String number){
        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    private static boolean isRoman(String number){
        char[] chars = number.toCharArray();

        if (!isEveryCharIsRoman(chars))
            return false;
        if (isAnyFourInRowAreEqual(chars))
            return false;
        if (isTwoLessThenNext(chars))
            return false;
        if (isAnyCharIsHalfOfNext(chars))
            return false;
        return true;
    }

    private static boolean isOperator(String str){
        char c = str.charAt(0);
        switch (c){
            case ('+'): return true;
            case ('-'): return true;
            case ('*'): return true;
            case ('/'): return true;
            default: return false;
        }
    }
    //endregion

    //region Auxiliary functions
    private static boolean isAnyFourInRowAreEqual(char[] chars){
        for (int i = 0; i < chars.length - 3; i++){
            if (chars[i] == chars[i + 1] &&
                    chars[i] == chars[i + 2] &&
                    chars[i] == chars[i + 3])
                return true;
        }
        return false;
    }

    private static boolean isEveryCharIsRoman(char[] chars){
        for (char c : chars) {
            if (convertRomanToInt(c) == -1)
                return false;
        }
        return true;
    }

    private static boolean isTwoLessThenNext(char[] chars){
        for (int i = 0; i < chars.length - 2; i++){
            int intA = convertRomanToInt(chars[i]);
            int intB = convertRomanToInt(chars[i + 1]);
            int intC = convertRomanToInt(chars[i + 2]);
            if (intB < intC && intA < intC)
                return true;
        }
        return false;
    }

    private static boolean isAnyCharIsHalfOfNext(char[] chars){
        for (int i = 0; i < chars.length - 1; i++){
            int a = convertRomanToInt(chars[i]);
            int b = convertRomanToInt(chars[i + 1]);
            if (a == b / 2)
                return true;
        }
        return false;
    }
    //endregion

    //region Converts
    private static int convertRomanToInt(char romanChar){
        switch (romanChar){
            case ('I'): return 1;
            case ('V'): return 5;
            case ('X'): return 10;
            case ('L'): return 50;
            case ('C'): return 100;
            case ('D'): return 500;
            case ('M'): return 1000;
            default: return -1;
        }
    }

    private static int convertRomanToInt(String number){
        char[] chars = number.toCharArray();
        int [] ints = new int[chars.length];
        for (int i = 0; i < chars.length; i ++){
            ints[i] = convertRomanToInt(chars[i]);
        }
        int result = 0;
        for (int i = 0; i < ints.length - 1; i ++){
            result += ints[i + 1] > ints[i] ? -ints[i] : ints[i];
        }
        result += ints[ints.length - 1];
        return result;
    }

    private static int convertArabicToInt(String number){
        return Integer.parseInt(number);
    }

    private static String convertIntToRoman(int number){
        if (associationsMap == null)
            associationsMap = createAssociationsMap();
        StringBuilder result = new StringBuilder();
        int M = number / 1000;
        int C = number / 100 % 10;
        int X = number / 10 % 10;
        int I = number % 10;
        for (int i = 0; i < M; i++)
            result.append("M");
        if (C != 0)
            result.append(associationsMap.get(C * 100));
        if (X != 0)
            result.append(associationsMap.get(X * 10));
        if (I != 0)
            result.append(associationsMap.get(I * 1));
        return result.toString();
    }

    //endregion

    //region AssociationsMap
    private static Map<Integer, String> associationsMap;

    private static Map<Integer, String> createAssociationsMap(){
        Map<Integer, String> associationsMap = new HashMap<Integer, String>();
        fillAssociationsMapCategory(1, "I", "V", "X", associationsMap);
        fillAssociationsMapCategory(10, "X", "L", "C", associationsMap);
        fillAssociationsMapCategory(100, "C", "D", "M", associationsMap);
        return  associationsMap;
    }

    private static void fillAssociationsMapCategory(int category, String symbolOne, String symbolFive, String symbolTen, Map<Integer, String> associationsMap){
        associationsMap.put(category, symbolOne);
        associationsMap.put(category * 2, symbolOne + symbolOne);
        associationsMap.put(category * 3, symbolOne + symbolOne + symbolOne);
        associationsMap.put(category * 4, symbolOne + symbolFive);
        associationsMap.put(category * 5, symbolFive);
        associationsMap.put(category * 6, symbolFive + associationsMap.get(category));
        associationsMap.put(category * 7, symbolFive + associationsMap.get(category * 2));
        associationsMap.put(category * 8, symbolFive + associationsMap.get(category * 3));
        associationsMap.put(category * 9, symbolOne + symbolTen);
    }

    //endregion
    enum NumberType {
        ARABIC,
        ROMAN,
        UNKNOWN
    }

}
