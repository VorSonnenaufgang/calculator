package application;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Pattern;

public class evaluate {

	private static String numPattern = "^(\\-|\\+)?\\d+(\\.\\d+)?$";
	private static String operandPattern = "\\(|\\)|\\*|/|\\+|-|!|\\^|��";
	private static ArrayList<Token> myTokens = new ArrayList<Token>();
	private static ArrayList<String> orator = new ArrayList<String>();
	private static ArrayList<String> orand = new ArrayList<String>();
	private Stack<CNode> lnodeStack = new Stack<>();
	private Stack<CBNode> bnodeStack = new Stack<>();
	private CTree ct = new CTree();

	private boolean confirm = true;

	// ʹ�ÿո�ָ��ַ���
	public String insetBlanks(String s) {
		s = s.replaceAll(" ", "");
		String result = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(' || s.charAt(i) == ')' || s.charAt(i) == '+' || s.charAt(i) == '*'
					|| s.charAt(i) == '/' || s.charAt(i) == '!' || s.charAt(i) == '^' || s.charAt(i) == '��')
				result += " " + s.charAt(i) + " ";
			else if (s.charAt(i) == '-') {
				int front = i - 1;
				while (front > 0 && s.charAt(front) == ' ') {
					front--;
				}
				if (front >= 0 && (s.charAt(front) == ')' || s.charAt(front) == '!' || s.charAt(front) == '^' || s.charAt(front) == '��' || (s.charAt(front) >= '0' && s.charAt(front) <= '9'))) {
					result += " " + s.charAt(i) + " ";
				} else {
					result += s.charAt(i);
				}
			} else
				result += s.charAt(i);
		}

		return result;
	}

	// ��������
	public ArrayList<String> analyzeExp(String curLine) {
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

		String newLine = curLine;
		int con = 0;

		System.out.println(tokens);

		for (int i = 0; i < tokens.size(); i++) {

			String token = tokens.get(i);
			if (token.length() == 0) {
				tokens.remove(i);
				i--;
			} else if (Pattern.matches(numPattern, token)) {
				orator.add(token);
				oratorS.push(token);

				double d = Double.parseDouble(token);

				if (d < 0) {
					newLine = newLine.replaceFirst("-", "@");
					con++;
				}

			} else if (Pattern.matches(operandPattern, token)) {
				orand.add(token);
				if (token.equals("(")) {
					brackets.push(token.charAt(0));
					if (i > 0 && Pattern.matches(numPattern, tokens.get(i - 1))) {
						confirm = false;
						System.err.println("����!\tλ�ã�" + con + " \tԭ��������ǰ����ֱ�ӼӲ�����");
					}
				} else if (token.equals(")")) {

					if (i < tokens.size() - 1 && Pattern.matches(numPattern, tokens.get(i + 1))) {
						confirm = false;
						System.err.println("����!\tλ�ã�" + (newLine.indexOf(token) + 1) + "\tԭ�������ź���ֱ�ӼӲ�����");
						newLine.replaceFirst("\\(", "@");
					}

					try {
						brackets.pop();
					} catch (Exception e) {
						confirm = false;

						System.err.println("����!\tλ�ã�" + newLine.indexOf(token) + "\tԭ����������δ�պ�");

						if (orand.indexOf("(") != -1) {
							orand.remove(orand.lastIndexOf("("));
						}
						orand.remove(orand.lastIndexOf(")"));
						tokens.remove(i);
						i--;
					}
				} else if (token.equals("!")) {
					if (i < tokens.size() - 1 && Pattern.matches(numPattern, tokens.get(i + 1))) {
						confirm = false;
						System.err.println("����!\tλ�ã�" + (newLine.indexOf(token) + 1) + "\tԭ�򣺽׳˷��ź���ֱ�ӼӲ�����");
					}
				} else if (token.equals("^")) {
					if (i < tokens.size() - 1 && Pattern.matches(numPattern, tokens.get(i + 1))) {
						confirm = false;
						System.err.println("����!\tλ�ã�" + (newLine.indexOf(token) + 1) + "\tԭ��ƽ�����Ų���ֱ�ӼӲ�����");
					}

				} else if (token.equals("��")) {
					if (i < tokens.size() - 1 && Pattern.matches(numPattern, tokens.get(i + 1))) {
						confirm = false;
						System.err.println("����!\tλ�ã�" + (newLine.indexOf(token) + 1) + "\tԭ�򣺿������ź���ֱ�ӼӲ�����");
					}
				} else {
					orandS.push(token);
					try {
						oratorS.pop();
					} catch (Exception e) {
						confirm = false;
						System.err.println("����!\tλ�ã�" + newLine.indexOf(token) + "\tԭ�򣺲�����ʹ�ô���");
						orand.remove(orand.lastIndexOf(token));
						tokens.remove(i);
						i--;

					}
				}

				if (token.charAt(0) != '(') {
					if (token.charAt(0) != '-') {
						newLine = newLine.replaceFirst("\\" + token, "@");
					} else {
						newLine = newLine.replaceFirst(token, "@");
					}
				}

			} else {
				if (tokens.indexOf(token) + 1 < tokens.size()) {
					tokens.remove(i + 1);
				}

				tokens.remove(i);
				i--;

				confirm = false;

				int pos = 0;
				for (int j = 0; j < token.length(); j++) {
					char c = token.charAt(j);
					if (c == '-' || c == '.' || (c >= '0' && c <= '9')) {
						continue;
					}
					pos = j;
					break;
				}
				confirm = false;
				System.err.println("����!\tλ�ã�" + (newLine.indexOf(token) + pos) + "\tԭ�����������Ч�ַ�");

				char b[] = new char[token.length()];
				Arrays.fill(b, 'c');
				String re = new String(b);
				newLine = newLine.replaceFirst(token, re);
			}
			con++;
		}

		while (!brackets.isEmpty()) {
			System.err.println("����!\tλ�ã�" + newLine.indexOf("(") + "\tԭ������δ�պ�");
			confirm = false;
			newLine = newLine.replaceFirst("\\(", "#");
			orand.remove(orand.lastIndexOf("("));
			tokens.remove(tokens.lastIndexOf("("));
			brackets.pop();
		}

		System.out.println("ԭʼ���ʽ��" + curLine);

		return tokens;

	}

	// ����������������ջ
	public String evaluateExp(String curLine) {

		ArrayList<String> tokens = analyzeExp(curLine);

		if (confirm) {
			Stack<Double> operandStack = new Stack<>();
			Stack<Character> operatorStack = new Stack<>();

			int con = 0;

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
					bnodeStack.push(new CBNode(token)); //�����������Ӧ��֦�ڵ�
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				// ��ǰ������ǳ˳����ж���������Ƿ��ǳ˳�������ǳ˳������㣬���Ǿ�ֱ����ջ
				else if (token.charAt(0) == '*' || token.charAt(0) == '/') {
					while (!operatorStack.isEmpty() && (operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
						processAnOperator(operandStack, operatorStack);
					}
					operatorStack.push(token.charAt(0)); // ��ǰ�������ջ
					bnodeStack.push(new CBNode(token)); //�����������Ӧ��֦�ڵ�
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				else if (token.charAt(0) == '!') {
					if(operandStack.isEmpty()) {
						confirm = false;
						System.err.println("�׳˷���ʹ�ô���!");
						return "Error";
					}
					double cur = operandStack.pop();
					CNode cn = lnodeStack.pop();
					double now = 1;
					if(cur<=0) {
						confirm = false;
						System.err.println("���ܼ��㸺���׳�!");
						return "Error";
					}
					else {
						for (int i = 1; i <= cur; i++) {
							if (now * i < Double.MAX_VALUE) {
								now *= i;
							} else {
								confirm = false;
								System.err.println("���!");
								return "Error";
							}

						}
					}
					
					operandStack.push(now);
					//�����Ӧ֦�ڵ�
					CBNode cb = new CBNode(token);
					cb.lChild = cn;
					cb.isSingle = true;
					lnodeStack.push(cb);
					ct.setRoot(cb);
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				} 
				else if (token.charAt(0) == '^') {
					if(operandStack.isEmpty()) {
						confirm = false;
						System.err.println("ƽ������ʹ�ô���!");
						return "Error";
					}
					double cur = operandStack.pop();
					CNode cn = lnodeStack.pop();
					if (con == 1 && cur < 0) {
						double now = -(cur * cur);
						if (now < Double.MAX_VALUE) {
							operandStack.push(-(cur * cur));
						} else {
							confirm = false;
							System.err.println("���!");
							return "Error";
						}
					} else {
						double now = cur * cur;
						if (now < Double.MAX_VALUE) {
							operandStack.push(cur * cur);
						} else {
							confirm = false;
							System.err.println("���!");
							return "Error";
						}
					}
					//�����Ӧ֦�ڵ�
					CBNode cb = new CBNode(token);
					cb.lChild = cn;
					cb.isSingle = true;
					lnodeStack.push(cb);
					ct.setRoot(cb);
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);

				} 
				else if (token.charAt(0) == '��') {
					if(operandStack.isEmpty()) {
						confirm = false;
						System.err.println("��������ʹ�ô���!");
						return "Error";
					}
					double cur = operandStack.pop();
					CNode cn = lnodeStack.pop();
					if (con == 1 && cur < 0) {
						operandStack.push(-Math.sqrt(-cur));
					} else {
						operandStack.push(Math.sqrt(cur));
					}
					//�����Ӧ֦�ڵ�
					CBNode cb = new CBNode(token);
					cb.lChild = cn;
					cb.isSingle = true;
					lnodeStack.push(cb);
					ct.setRoot(cb);
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
					//�������ֶ�Ӧ��Ҷ�ӽڵ�
					CLNode cNode = new CLNode(Double.toString(value));
					lnodeStack.push(cNode);
				}
				con++;
			}

			// ��ջ�в��ǿյ�ʱ���������
			while (!operatorStack.isEmpty()) {
				processAnOperator(operandStack, operatorStack);
			}
			
			ct.setRoot(lnodeStack.pop());

			DecimalFormat df = new DecimalFormat("0.000000000000");
			String res = df.format(operandStack.pop());
			res = trim(res, '0');
			if (res.charAt(res.length() - 1) == '.') {
				res = res.substring(0, res.length() - 1);
			}

			return res;
		} 
		else {
			return "Error";
		}
	}

	// ����ջ�е��������ݣ���ջ�е�������������֮��Ľ���洢��ջ��
	public void processAnOperator(Stack<Double> operandStack, Stack<Character> operatorStack) {
		char op = operatorStack.pop(); // ����һ��������
		double op1 = operandStack.pop(); // �������������������Ͳ�����op��������
		double op2 = operandStack.pop();
		double res = 0.0;
		if (op == '+' && op1 + op2 < Double.MAX_VALUE)
			operandStack.push(op1 + op2);
		else if (op == '-' && op1 + op2 < Double.MAX_VALUE)
			operandStack.push(op2 - op1);
		else if (op == '*' && op1 + op2 < Double.MAX_VALUE)
			operandStack.push(op1 * op2);
		else if (op == '/' && op1 + op2 < Double.MAX_VALUE)
			operandStack.push(op2 / op1);
		else {
			confirm = false;
			System.err.println("���!");
		}

		CBNode cb = bnodeStack.pop();
		CNode cn1 = lnodeStack.pop();
		CNode cn2 = lnodeStack.pop();
		cb.lChild = cn2;
		cb.rChild = cn1;
		lnodeStack.push(cb);
		ct.setRoot(cb);
	}
	
	protected static class CLNode extends CNode{
		private String text;
		private double value;

		public CLNode(String txt) {
			super(txt);
			value = Double.valueOf(txt);
		}

		public double getValue() {
			return value;
		}
		
		public boolean isLeaf() {
			return true;
		}

	}
	
	protected static class CBNode extends CNode{
		private String text;

		public CBNode(String txt) {
			super(txt);
		}

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

	public String trim(String source, char trimChar) {
		int tail = source.length() - 1;

		while (tail > 0 && source.charAt(tail) == '0') {
			source = source.substring(0, source.length() - 1);
			tail--;
		}

		return source;
	}
	
	public CTree getTree() {
		return ct;
	}
	
	public void printTree() {
		if(confirm) {
			scanTree(ct.getRoot(), 1);
		}else {
			System.out.println("�﷨�����Ϲ淶�����﷨�������");
		}
	}
	
	private void scanTree(CNode cn, int depth) {
		if(!cn.isLeaf()) {
			scanTree(cn.lChild, depth + 1);
			System.out.println("��" + depth + "��ڵ㣺" + cn.getText());
			if(!cn.isSingle) {
				scanTree(cn.rChild, depth + 1);
			}
		}else {
			System.out.println("��" + depth + "��ڵ㣺" + cn.getText());
		}
	}
}
