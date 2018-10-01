package application;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * ���ܳ��ֵĴ��� 
 * 1.����Ƿ��ַ� 
 * 2.���Ų��պ� 
 * 3.�����Ĳ����� 
 * 4.
 */

public class calculator {

	String numPattern = "^(\\-|\\+)?\\d+(\\.\\d+)?$";
	String operandPattern="\\(|\\)|\\*|/|\\+|-";
	
	boolean confirm=true;

	private static ArrayList<Token> myTokens = new ArrayList<Token>();
	private static ArrayList<String> orator = new ArrayList<String>();
	private static ArrayList<String> orand = new ArrayList<String>();

	/**
	 * �������ʽ���㴦��˼·�� ��������ҵ�ɨ����ʽ����ȡ�������������������Լ����� 1.����ǲ�������ֱ�ӽ���ѹ��operandStackջ��
	 * 2.�����+��-�����������operatorStackջ���е������������������֮����ȡ���������ѹ��ջ��
	 * 3.�����*,/�����������ջ��������*,/������������ʱ��ջ�����������+��-��ôֱ����ջ��������֮����ȡ�����������ջ
	 * 4.�����(,��ôֱ��ѹ��operatorStackջ�� 5.�����),�ظ���������operatorStackջ�����������֪������ջ����(
	 */

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
	
	public ArrayList<String> analyzeExp(String expression) {
		String newExpression = insetBlanks(expression);
		String[] tokens1 = newExpression.split(" ");
		
		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(tokens1));
		Stack<Character> brackets = new Stack<>();
		
		for(int i=0;i<tokens.size();i++) {
			String token=tokens.get(i);				
			if (token.length() == 0) {
				tokens.remove(i);
				i--;
			}	
			else if(Pattern.matches(numPattern, token)) {
				orator.add(token);
			}
			else if(Pattern.matches(operandPattern, token)) {
				orand.add(token);
				if(token.equals("(")) {
					brackets.push(token.charAt(0));
				}
				else if(token.equals(")")){
					try {
						brackets.pop();
					}
					catch(Exception e) {
						confirm=false;
						System.err.println("����! λ�ã�"+expression.indexOf(token)+" ԭ����������δ�պ�");
						if(orand.indexOf("(")!=-1) {
							orand.remove(orand.lastIndexOf("("));
						}
						orand.remove(orand.lastIndexOf(")"));
					}
				}
			}
			else {			
				tokens.remove(i);
				i--;
				if(tokens.indexOf(token)+1<tokens.size()) {
					tokens.remove(i+1);
				}
				confirm=false;
				System.err.println("����! λ�ã�"+expression.indexOf(token)+" ԭ�����������Ч�ַ�");
			}
		}
		
		System.out.println("ԭʼ���ʽ��"+expression);
		System.out.println("��������"+orator);
		System.out.println("��������"+orand);
		System.out.println("��token��"+tokens);
		
		return tokens;
		
	}
	
	// ����������������ջ
	public String evaluateExp(String expression) {
		
		ArrayList<String> tokens=analyzeExp(expression);
		
		if(confirm) {
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

			DecimalFormat df = new DecimalFormat("0.00000000000000000");
			String res = df.format(operandStack.pop());
			res = trim(res, '0');
			
			return res;// ������
		}
		
		else {
			return "";
		}
	
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
