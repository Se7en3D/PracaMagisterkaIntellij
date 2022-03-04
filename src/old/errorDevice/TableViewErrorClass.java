package old.errorDevice;

import java.lang.reflect.Array;

public class TableViewErrorClass {
    private int codeNumber;
    private String variableInProgram;
    private String source;
    private String comment;


    public TableViewErrorClass(int codeNumber, String variableInProgram, String source, String comment) {
        this.codeNumber = codeNumber;
        this.variableInProgram = variableInProgram;
        this.source = source;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return codeNumber +" ; " + variableInProgram + " ; " + source + " ; " + comment + '\n';
    }

    public int getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(int codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getVariableInProgram() {
        return variableInProgram;
    }

    public void setVariableInProgram(String variableInProgram) {
        this.variableInProgram = variableInProgram;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
