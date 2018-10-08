package application;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

/**
 * ���ܳ��ֵĴ��� 1.����Ƿ��ַ� 2.���Ų��պ� 3.�����Ĳ����� 4.
 */

public class calculator implements Initializable {

	@FXML
	private Label textResult;

	@FXML
	private ScrollPane scrollResult;

	private static String numPattern = "^(\\-|\\+)?\\d+(\\.\\d+)?$";
	private static String operandPattern = "\\(|\\)|\\*|/|\\+|-";

	private boolean confirm = true;

	private static ArrayList<Token> myTokens = new ArrayList<Token>();
	private static ArrayList<String> orator = new ArrayList<String>();
	private static ArrayList<String> orand = new ArrayList<String>();

    private static String preLines;
    private static String curLine;

	/**
	 * �������ʽ���㴦��˼·�� ��������ҵ�ɨ����ʽ����ȡ�������������������Լ����� 1.����ǲ�������ֱ�ӽ���ѹ��operandStackջ��
	 * 2.�����+��-�����������operatorStackջ���е������������������֮����ȡ���������ѹ��ջ��
	 * 3.�����*,/�����������ջ��������*,/������������ʱ��ջ�����������+��-��ôֱ����ջ��������֮����ȡ�����������ջ
	 * 4.�����(,��ôֱ��ѹ��operatorStackջ�� 5.�����),�ظ���������operatorStackջ�����������֪������ջ����(
	 */

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// TODO (don't really need to do anything here).
        String textAll = textResult.getText();
        int currentLine = 0;
        for(int i=0; i<textAll.length();i++){
            if(textAll.charAt(i) == '\n'){
                currentLine = i + 1;
            }
        }
        preLines = textAll.substring(0, currentLine);
        if(currentLine == textAll.length()){
            curLine = "";
        }else{
            curLine = textAll.substring(currentLine, textAll.length());
        }

	}

	// ʹ�ÿո�ָ��ַ���
	public String insetBlanks(String s) {
		String result = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(' || s.charAt(i) == ')' || s.charAt(i) == '+' || s.charAt(i) == '*'
					|| s.charAt(i) == '/')
				result += " " + s.charAt(i) + " ";
			else if (s.charAt(i) == '-') {
				int front = i - 1;
				while (front > 0 && s.charAt(front) == ' ') {
					front--;
				}
				if (front > 0 && (s.charAt(front) == ')' || (s.charAt(front) >= '0' && s.charAt(front) <= '9'))) {
					result += " " + s.charAt(i) + " ";
				} else {
					result += s.charAt(i);
				}
			} else
				result += s.charAt(i);
		}

		return result;
	}

	public void checkChar() {

	}

	public ArrayList<String> analyzeExp() {
	    confirm = true;
	    myTokens.clear();
	    orand.clear();
	    orator.clear();
		String newExpression = insetBlanks(curLine);
		String[] tokens1 = newExpression.split(" ");

		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(tokens1));
		
		Stack<Character> brackets = new Stack<>();
		Stack<String> orandS = new Stack<>();
		Stack<String> oratorS = new Stack<>();
		
		String newLine=curLine;

		
		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if (token.length() == 0) {
				tokens.remove(i);
				i--;
			} else if (Pattern.matches(numPattern, token)) {
				orator.add(token);
				oratorS.push(token);
				
				double d=Double.parseDouble(token);
								
				if(d<0) {
					newLine=newLine.replaceFirst("-", "@");
				}
				
			} else if (Pattern.matches(operandPattern, token)) {
				orand.add(token);		
				if (token.equals("(")) {
					brackets.push(token.charAt(0));
				} else if (token.equals(")")) {
					try {
						brackets.pop();
					} catch (Exception e) {
						confirm = false;
						
						System.err.println("����! λ�ã�" + newLine.indexOf(token) + " ԭ����������δ�պ�");
						
						if (orand.indexOf("(") != -1) {
							orand.remove(orand.lastIndexOf("("));
						}
						orand.remove(orand.lastIndexOf(")"));
						tokens.remove(i);
						i--;
					}
				} else {
					orandS.push(token);			
					try {
						oratorS.pop();
					} catch(Exception e) {
						confirm = false;
						System.err.println("����! λ�ã�" + newLine.indexOf(token) + " ԭ�򣺲�����ʹ�ô���");
						orand.remove(orand.lastIndexOf(token));
						tokens.remove(i);
						i--;
					
					}
				}
				
				if(token.charAt(0)!='(') {
					if(token.charAt(0)!='-') {
						newLine=newLine.replaceFirst("\\" +token, "@");
					}
					else {
						newLine=newLine.replaceFirst(token, "@");
					}	
				}
				
				
			} else {
				if (tokens.indexOf(token) + 1 < tokens.size()) {
					tokens.remove(i + 1);
				}
				
				tokens.remove(i);
				i--;
				
				confirm = false;
				
				int pos=0;
				for(int j=0;j<token.length();j++) {
					char c=token.charAt(j);
					if(c=='-'||c=='.'||(c>='0' && c<='9')) {
						continue;
					}
					pos=j;
					break;
				}			
				confirm = false;		
				System.err.println("����! λ�ã�" + (newLine.indexOf(token)+pos) + " ԭ�����������Ч�ַ�");
				
				char b[] = new char[token.length()];
			    Arrays.fill(b, 'c');
			    String re = new String(b);
				newLine=newLine.replaceFirst(token, re);
			}		
		}
		
		
		
		while(!brackets.isEmpty()) {	
			System.err.println("����! λ�ã�" + newLine.indexOf("(") + " ԭ������δ�պ�");
			confirm = false;
			newLine=newLine.replaceFirst("\\(", "#");
			orand.remove(orand.lastIndexOf("("));
			tokens.remove(tokens.lastIndexOf("("));
			brackets.pop();		
		}
		
		System.out.println("ԭʼ���ʽ��" + curLine);
		System.out.println("��������" + orator);
		System.out.println("��������" + orand);
		System.out.println("��token��" + tokens);

		return tokens;

	}

    @FXML
    public void pressNum(ActionEvent event){
        Object btnNum=event.getSource();
        Button btnCur = (Button) btnNum;
        if(curLine.equals("0")){
            curLine = "";
        }
        curLine += btnCur.getText();
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void pressOperand(ActionEvent event){
        Object btnNum=event.getSource();
        Button btnCur = (Button) btnNum;
        String operandPress = btnCur.getText();
        if(operandPress.equals("��")){
            curLine += "+";
        }else if(operandPress.equals("��")){
            curLine += "-";
        }else if(operandPress.equals("��")){
            curLine += "*";
        }else if(operandPress.equals("��")){
            curLine += "/";
        }else{
            curLine += operandPress;
        }

        textResult.setText(preLines + curLine);
    }

    @FXML
    public void pressBackspace(ActionEvent event){
        curLine = curLine.substring(0, curLine.length() - 1);
        if(curLine.length() == 0){
            curLine = "0";
        }
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void clearAll(ActionEvent event){
	    preLines = "";
	    curLine = "0";
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void clearError(ActionEvent event){
        curLine = "0";
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void toNegative(ActionEvent event){
	    if(trim(curLine, '0').length() == 0) return;
	    int lastNum = curLine.length() - 1;
	    while(lastNum >= 0 && Character.isDigit(curLine.charAt(lastNum))){
	        lastNum--;
        }
        StringBuilder cl = new StringBuilder(curLine);
	    if(lastNum >= 0 && cl.charAt(lastNum) == '-' && !Character.isDigit(cl.charAt(lastNum - 1))){
	        cl.deleteCharAt(lastNum);
        }else{
            cl.insert(lastNum + 1, '-');
        }
	    curLine = new String(cl);
        textResult.setText(preLines + curLine);
    }

	// ����������������ջ
	@FXML
	public void evaluateExp(ActionEvent event) {

		ArrayList<String> tokens = analyzeExp();

		if (confirm) {
			Stack<Double> operandStack = new Stack<>();
			Stack<Character> operatorStack = new Stack<>();

			for (String token : tokens) {
				if (token.length() == 0) // ����ǿո�ͼ���ѭ��
					continue;
				// ��ǰ������ǼӼ�,���۲�����ջ��ʲô�������Ҫ����
				else if (token.length() == 1 && (token.charAt(0) == '+' || token.charAt(0) == '-')) {
					// ��ջ��Ϊ�գ�����ջ���������һ��Ԫ���ǼӼ��˳�������һ��
					while (!operatorStack.isEmpty() && (operatorStack.peek() == '-' || operatorStack.peek() == '+'
							|| operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
						processAnOperator(operandStack, operatorStack); // ��ʼ����
					}
					operatorStack.push(token.charAt(0)); // ��ǰ�������ջ
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				// ��ǰ������ǳ˳����ж���������Ƿ��ǳ˳�������ǳ˳������㣬���Ǿ�ֱ����ջ
				else if (token.charAt(0) == '*' || token.charAt(0) == '/') {
					while (!operatorStack.isEmpty() && (operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
						processAnOperator(operandStack, operatorStack);
					}
					operatorStack.push(token.charAt(0)); // ��ǰ�������ջ
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				// ��ǰ������������ţ�ֱ����ջ
				else if (token.trim().charAt(0) == '(') {
					operatorStack.push('(');
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				// ��ǰ������������ŵģ����ջ�е������ֱ��������
				else if (token.trim().charAt(0) == ')') {
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
					while (operatorStack.peek() != '(') {
						processAnOperator(operandStack, operatorStack);// ��ʼ����
					}
					operatorStack.pop();// ���������
				}
				// ��ǰ�����ֵĻ���ֱ��������ջ
				else {
					boolean isNegative = false;
					double value = 0.0;
					if (token.charAt(0) == '-') {
						isNegative = true;
						token = token.substring(1, token.length());
					}
					value = Double.parseDouble(token);
					if (isNegative)
						value = -value;
					operandStack.push(value);// �����ַ���ת�������֣�ѹ��ջ��
					OperandToken newToken = new OperandToken(value);
					myTokens.add(newToken);
				}
			}

			// ��ջ�в��ǿյ�ʱ���������
			while (!operatorStack.isEmpty()) {
				processAnOperator(operandStack, operatorStack);
			}

			printOutTokens();

			DecimalFormat df = new DecimalFormat("0.000000000000");
			String res = df.format(operandStack.pop());
			res = trim(res, '0');
			if(res.charAt(res.length() - 1) == '.'){
			    res = res.substring(0, res.length() - 1);
            }

			preLines += curLine + "\n";
            curLine = res;
			textResult.setText(preLines + curLine);// ������
		}
		else {
            preLines += curLine + "\n";
            curLine = "0";
            textResult.setText(preLines + "Error");
		}
		scrollToBottom();
	}

	public void scrollToBottom(){
	    scrollResult.setVvalue(1);
    }

	public void printOutTokens() {
		if (myTokens.size() == 0) {
			System.out.println("No tokens");
			return;
		}

		for (Token token : myTokens) {
			if (token.isOperand()) {
				System.out.println("Operand: " + token.getText());
			} else if (token.isOperator()) {
				System.out.println("Operator: " + token.getText());
			}
		}

		System.out.print("Token sequence: ");
		for (Token token : myTokens) {
			System.out.print(" " + token.getText() + " ");
		}
		System.out.println();

	}

	public String trim(String source, char trimChar) {
		int tail = source.length() - 1;

		while (tail > 0 && source.charAt(tail) == '0') {
			source = source.substring(0, source.length() - 1);
			tail--;
		}

		return source;
	}

	// ����ջ�е��������ݣ���ջ�е�������������֮��Ľ���洢��ջ��
	public void processAnOperator(Stack<Double> operandStack, Stack<Character> operatorStack) {
		char op = operatorStack.pop(); // ����һ��������
		double op1 = operandStack.pop(); // �������������������Ͳ�����op��������
		double op2 = operandStack.pop();
		if (op == '+')
			operandStack.push(op1 + op2);
		else if (op == '-')
			operandStack.push(op2 - op1);
		else if (op == '*')
			operandStack.push(op1 * op2);
		else if (op == '/')
			operandStack.push(op2 / op1);
	}

	protected static class OperandToken extends Token {
		private double value;

		protected OperandToken(double d) {
			value = d;
		}

		public boolean isOperand() {
			return true;
		}

		public double getValue() {
			return value;
		}

		public String getText() {
			return Double.toString(value);
		}
	}

	protected static class OperatorToken extends Token {
		private String ope;

		protected OperatorToken(String s) {
			ope = s;
		}

		public boolean isOperator() {
			return true;
		}

		public String getText() {
			return ope;
		}
	}

}
