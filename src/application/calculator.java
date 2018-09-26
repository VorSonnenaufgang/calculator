package application;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;
import javax.script.*;  

public class calculator{
	
	/**���봦��˼·��
	 * ����������Ƿ���"="��Ϊ��ֵ���ʽ��������ʽ�ֱ���
	 * 1.��ϵ�����ģ���bindings�������ı��������Ӧֵ���룬��û�У����½�
	 * 2.���ڸ�ֵ���ʽ�����������淶���ָ�Ϊ�������ͼ�����ʽ������
	 * 3.���㵱ǰ��������Ӧ���������ʽ��put��һ�Լ�ֵ
	 * 4.����������ʽ����Ϊ���ʽ����ͱ�����������ʽ�������б��ʽ���㣬������������������Ӧ��ֵ*/
	
	/**�쳣����˼·��
	 * 1.���ʽ�淶���ԷֺŽ�β���������Error:expression with no semicolon.
	 * 2.�������淶�������ֺ���ĸ��ɣ�������ĸ��ͷ���������Error:incorrect variable name.
	 * 3.����淶������������ʽ����/0����������Error:Wrong calculation with "/0".
	 * 4.����淶������������ʽ������δ����ı���������Error:undefined identifier.*/ 	
	
	int line=0;//��¼����
  	int position=0;//��¼λ��
  	String varPa="[a-zA-Z$][a-zA-Z0-9$]*";//�������淶
	String expPa=".*;";//���ʽ�淶
  
    
	
    /**�������ʽ���㴦��˼·��
     * ��������ҵ�ɨ����ʽ����ȡ�������������������Լ�����
     * 1.����ǲ�������ֱ�ӽ���ѹ��operandStackջ��
     * 2.�����+��-�����������operatorStackջ���е������������������֮����ȡ���������ѹ��ջ��
     * 3.�����*,/�����������ջ��������*,/������������ʱ��ջ�����������+��-��ôֱ����ջ��������֮����ȡ�����������ջ
     * 4.�����(,��ôֱ��ѹ��operatorStackջ��
     * 5.�����),�ظ���������operatorStackջ�����������֪������ջ����(
     */
    
	//ʹ�ÿո�ָ��ַ���
    public String insetBlanks(String s) {
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == ')' || s.charAt(i) == '+' || s.charAt(i) == '-'|| s.charAt(i) == '*' || s.charAt(i) == '/')
                result += " " + s.charAt(i) + " ";
            else
                result += s.charAt(i);
        }
        return result;
    }

    //����������������ջ
    public double evaluateExp(String expression) {
        Stack<Double> operandStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();
        expression = insetBlanks(expression);
        String[] tokens = expression.split(" ");
        for (String token : tokens) {
            if (token.length() == 0)   //����ǿո�ͼ���ѭ��
                continue;
            //��ǰ������ǼӼ�,���۲�����ջ��ʲô�������Ҫ����
            else if (token.charAt(0) == '+' || token.charAt(0) == '-') {
                //��ջ��Ϊ�գ�����ջ���������һ��Ԫ���ǼӼ��˳�������һ��
                while (!operatorStack.isEmpty()&&(operatorStack.peek() == '-' || operatorStack.peek() == '+' || operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
                    processAnOperator(operandStack, operatorStack);   //��ʼ����
                }
                operatorStack.push(token.charAt(0));   //��ǰ�������ջ
            }
            //��ǰ������ǳ˳����ж���������Ƿ��ǳ˳�������ǳ˳������㣬���Ǿ�ֱ����ջ
            else if (token.charAt(0) == '*' || token.charAt(0) == '/') {
                while (!operatorStack.isEmpty()&&(operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
                    processAnOperator(operandStack, operatorStack);
                }
                operatorStack.push(token.charAt(0));   //��ǰ�������ջ
            }
            //��ǰ������������ţ�ֱ����ջ
            else if (token.trim().charAt(0) == '(') {
                operatorStack.push('(');
            }
            //��ǰ������������ŵģ����ջ�е������ֱ��������
            else if (token.trim().charAt(0) == ')') {
                while (operatorStack.peek() != '(') {
                    processAnOperator(operandStack, operatorStack);//��ʼ����
                }
                operatorStack.pop();//���������
            }
            //��ǰ�����ֵĻ���ֱ��������ջ
            else {
                operandStack.push(Double.parseDouble(token));//�����ַ���ת�������֣�ѹ��ջ��
            }
        }
        //��ջ�в��ǿյ�ʱ���������
        while (!operatorStack.isEmpty()) {
            processAnOperator(operandStack, operatorStack);
        }
        return operandStack.pop();//������
    }

    
    //����ջ�е��������ݣ���ջ�е�������������֮��Ľ���洢��ջ��
    public void processAnOperator(Stack<Double> operandStack, Stack<Character> operatorStack) {
        char op = operatorStack.pop(); //����һ��������
        double op1 = operandStack.pop(); //�������������������Ͳ�����op��������
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
    
}
