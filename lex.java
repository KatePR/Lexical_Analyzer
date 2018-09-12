import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class lex {

    public static void main(String args[]) throws FileNotFoundException
    {

  /* the following code to test the lexical analyzer
     will be discarded in the next stage
  */


        LexAnalyzer lexan=new LexAnalyzer();
        lexan.getToken();
        PrintWriter lexout=new PrintWriter("lexout.txt");

        while (! (lexan.token == tokenType.id
                && lexan.lexeme.equals("done")))
        { lexout.print(Globs.tokenNameStrings[lexan.token.ordinal()]);
            if (lexan.token == tokenType.id) lexout.print("     "+lexan.lexeme);
            if (lexan.token == tokenType.number) lexout.print("     "+lexan.value);
            lexout.println();
            lexout.flush();
            lexan.getToken();
        } //end while

    } //end main

} //end lex


enum tokenType       // give a name to each terminal
{ mainToken,    valToken,     refToken,   intToken,
    readToken,    writeToken,   callToken,  ifToken,
    elseToken,    endifToken,   whileToken, id,
    number,       leftParen,    rightParen, leftBrace,
    rightBrace,   comma,        semicolon,  assignment,
    equality,     notEqual,     lessToken,  lessEqual,
    greater,      greaterEqual, plusToken,  minusToken,
    times,        slash
};

class Globs
{
    //table of token names as strings. for use in debugging.

    static String[] tokenNameStrings=
            {"mainToken    ", "valToken     ", "refToken     ", "intToken     ",
                    "readToken    ", "writeToken   ", "callToken    ", "ifToken      ",
                    "elseToken    ", "endifToken   ", "whileToken   ", "id           ",
                    "number       ", "leftParen    ", "rightParen   ", "leftBrace    ",
                    "rightBrace   ", "comma        ", "semicolon    ", "assignment   ",
                    "equality     ", "notEqual     ", "lessToken    ", "lessEqual    ",
                    "greater      ", "greaterEqual ", "plusToken    ", "minusToken   ",
                    "times        ", "slash        "
            };

    static void error(int code)
    { switch ( code )
    { case  0: System.out.println("error type 0 at line "+
            SourceFileReader.linecount+
            ": program incomplete\n");
        System.exit(0);

        case  1: System.out.println("error type 1 at line "+
                SourceFileReader.linecount+
                ": unknown symbol\n");
            System.exit(0);


    } //end switch

    } //end function error

}  //end Globs


class SourceFileReader
{
    static int  linecount=1;  // number of lines read
    Scanner source;
    char letter;
    boolean EOF;

    SourceFileReader() throws FileNotFoundException
    {
        source=new Scanner(new File("source.bc"));
        source.useDelimiter("");
        letter=' ';
        EOF=false;
    }

    void getCharacter()
    { if (EOF) Globs.error(0);   //program incomplete, halt compiler
        if (! source.hasNext())
        { EOF=true; //sets end-of-file flag
            letter=' ';
            return;
        }
        letter=source.next().charAt(0);   //reads one character from file

        System.out.print(letter); //for debugging

        if ( letter == '\n') { letter= ' ';   linecount++; }
        else if ( letter == '\t'||letter=='\r') letter= ' ';
    } // end getCharacter
} //end SourceFileReader

class LexAnalyzer
{
    tokenType token;    // lookahead token
    String lexeme;        // spelling of identifier token
    int  value;         // value of number token

    SourceFileReader source;

    LexAnalyzer() throws FileNotFoundException
    {
        source=new SourceFileReader();
    }

    class reservedWord
    { String spelling;
        tokenType kind;

        reservedWord(String s, tokenType t)
        {spelling=s; kind=t;}

    }

    reservedWord[] reservedWords =
            { new reservedWord("call",      tokenType.callToken),
                    new reservedWord("else",      tokenType.elseToken),
                    new reservedWord("endif",     tokenType.endifToken),
                    new reservedWord("if",        tokenType.ifToken),
                    new reservedWord("int",       tokenType.intToken),
                    new reservedWord("main",      tokenType.mainToken),
                    new reservedWord("read",      tokenType.readToken),
                    new reservedWord("ref",       tokenType.refToken),
                    new reservedWord("val",       tokenType.valToken),
                    new reservedWord("while",     tokenType.whileToken),
                    new reservedWord("write",     tokenType.writeToken)
            };

    void getToken()

    //This is the function that I had to code for the Lexical Analyzer.

    /*****THIS IS THE FUNCTION YOU SHALL WRITE*****)
     *the main tasks of the function are
     1. Skip all blank characters preceding a token,


     2. If the first character of a token is a letter,
     then find the character string over a..z and 0..9.
     Search table of reserved words to determine whether
     the string is a reserved word and set the global variable token.


     3. If the first character of a token is in 0..9,
     then the token must be a number. Find the numeral string
     and convert it into an integer for the global variable value.


     4. Other cases are quite easy */

  /* the main trick is probably that getToken always
     keeps a single lookahead character. That is, after
     finding each token, we should always make sure that the global
     variable letter contains the character that immediately follows the
     token, no matter what that character is. This technique
     will simplify the code */ {

        int i = 0, k;
        while (source.letter == ' ') source.getCharacter();  //skip blanks

        String cases = "unknown";

        if (Character.isLetter(source.letter)){
            cases = "letter";
        }
        else if(Character.isDigit(source.letter)){
            cases = "number";
        }
        else {
            cases = Character.toString(source.letter);
        }

        switch (cases){
            //case for letters
            case  "letter":
                lexeme = Character.toString(source.letter);
                source.getCharacter();
                    while (Character.isLetter(source.letter) || Character.isDigit(source.letter)){
                        lexeme += Character.toString(source.letter);
                        token = tokenType.id;
                        source.getCharacter();
                }
                //single character id
                if(lexeme.length() == 1){
                    token = tokenType.id;
                }
                //will not recognize symbols that are uppercase
                if(!lexeme.equals(lexeme.toLowerCase())) {
                    Globs.error(1);
                }

                //checks to see if the string is a reserved word
                for(int x = 0; x < reservedWords.length; x++){
                    if (reservedWords[x].spelling.equals(lexeme)){
                        token = reservedWords[x].kind;
                    }
                }
                break;

            //case for numbers
            case "number":
                String intValue = "";
                while (Character.isDigit(source.letter)){
                        intValue += source.letter;
                        source.getCharacter();
                    }
                    value = Integer.valueOf(intValue);
                    token = tokenType.number;
                    break;

            //cases for tokens
            case "(":
                token = tokenType.leftParen;
                source.getCharacter();
                break;

            case ")":
                token = tokenType.rightParen;
                source.getCharacter();
                break;

            case "{":
                token = tokenType.leftBrace;
                source.getCharacter();
                break;

            case "}":
                token = tokenType.rightBrace;
                source.getCharacter();
                break;

            case ",":
                token = tokenType.comma;
                source.getCharacter();
                break;

            case ";":
                token = tokenType.semicolon;
                source.getCharacter();
                break;

            case "=":
                source.getCharacter();
                if(source.letter == '=') {
                    token = tokenType.assignment;
                    source.getCharacter();
                }else if(token != tokenType.assignment){
                    token = tokenType.equality;
                }
                else{
                    Globs.error(1);
                }
                break;

            case "!":
                source.getCharacter();
                if(source.letter == '=') {
                    token = tokenType.notEqual;
                    source.getCharacter();
                }
                else{
                    Globs.error(1);
                }
                break;

            case "<":
                source.getCharacter();
                if(source.letter == '=') {
                    token = tokenType.lessEqual;
                    source.getCharacter();
                }
                else if(token != tokenType.lessEqual){
                    token = tokenType.lessToken;
                }else{
                    Globs.error(1);
                }
                break;

            case ">":
                source.getCharacter();
                if(source.letter == '=') {
                    token = tokenType.greaterEqual;
                    source.getCharacter();
                }
                else if(token != tokenType.greaterEqual){
                    token = tokenType.greater;
                }else{
                    Globs.error(1);
                }
                break;

            case "+":
                token = tokenType.plusToken;
                source.getCharacter();
                break;

            case "-":
                token = tokenType.minusToken;
                source.getCharacter();
                break;

            case "*":
                token = tokenType.times;
                source.getCharacter();
                break;

            case "/":
                token = tokenType.slash;
                source.getCharacter();
                break;

            default:
                Globs.error(1);
                break;

        } //The above solution for the getToken function was developed by Kate Rader.

    } //end function getToken

} //end class LexAnalyzer