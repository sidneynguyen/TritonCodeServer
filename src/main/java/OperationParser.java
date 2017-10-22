import java.util.StringTokenizer;

public class OperationParser {
    public static Operation strToOperation(String opStr) {
        StringTokenizer tokenizer = new StringTokenizer(opStr, "" + (char) 7);
        Operation operation = new Operation();
        while (tokenizer.hasMoreElements()) {
            String unparsedOp = (String) tokenizer.nextElement();
            if (unparsedOp.charAt(0) == 'R') {
                int length = Integer.parseInt(unparsedOp.substring(1));
                operation.add(new OperationComponent(OperationComponent.OP_COMP_RETAIN, null, length));
            } else if (unparsedOp.charAt(0) == 'I') {
                String value = unparsedOp.substring(1);
                operation.add(new OperationComponent(OperationComponent.OP_COMP_INSERT, value, value.length()));
            } else if (unparsedOp.charAt(0) == 'D') {
                String value = unparsedOp.substring(1);
                operation.add(new OperationComponent(OperationComponent.OP_COMP_DELETE, value, value.length()));
            }
        }
        return operation;
    }

    public static String operationToStr(Operation operation) {
        String opStr = "";
        for (int i = 0; i < operation.size(); i++) {
            OperationComponent op = operation.get(i);
            if (op.getOperationType() == OperationComponent.OP_COMP_INSERT) {
                opStr += "I" + op.getValue() + (char) 7;
            } else if (op.getOperationType() == OperationComponent.OP_COMP_DELETE) {
                opStr += "D" + op.getValue() + (char) 7;
            } else {
                opStr += "R" + op.getLength() + (char) 7;
            }
        }
        return opStr;
    }
}
