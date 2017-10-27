package edu.ycp.cs340.jsonparser;

public class JSONParser {
	private Lexer lexer;

	public JSONParser(Lexer lexer) {
		this.lexer = lexer;
	}

	public Node parseValue() {
		Node value = new Node(Symbol.VALUE);

		Token tok = lexer.peek();

		if (tok == null) {
			throw new ParserException("Unexpected end of input reading value");
		}

		if (tok.getSymbol() == Symbol.STRING_LITERAL) {
			// Value → StringLiteral
			value.getChildren().add(expect(Symbol.STRING_LITERAL));
		} else if (tok.getSymbol() == Symbol.INT_LITERAL) {
			// Value → IntLiteral
			value.getChildren().add(expect(Symbol.INT_LITERAL));
		} else if (tok.getSymbol() == Symbol.LBRACE) {
			// Value → Object
			value.getChildren().add(parseObject());
		} else if (tok.getSymbol() == Symbol.LBRACKET) {
			// Value → Array
			value.getChildren().add(parseArray());
		} else {
			throw new ParserException("Unexpected token looking for value: " + tok);
		}

		return value;
	}

	private Node parseObject() {
		Node object = new Node(Symbol.OBJECT);

		// Object → "{" OptFieldList "}"
		object.getChildren().add(expect(Symbol.LBRACE));
		object.getChildren().add(parseOptFieldList());
		object.getChildren().add(expect(Symbol.RBRACE));

		return object;
	}

	private Node parseOptFieldList() {
		Node FieldList = new Node(Symbol.OPT_FIELD_LIST);
		Token tok = lexer.peek();
		if (tok.getSymbol() == Symbol.RBRACE) {
		} else {
			FieldList.getChildren().add(parseFieldList());
		}
		return FieldList;
	}

	private Node parseArray() {
		Node array = new Node(Symbol.ARRAY);
		array.getChildren().add(expect(Symbol.LBRACKET));
		array.getChildren().add(parseOptValueList());
		array.getChildren().add(expect(Symbol.RBRACKET));
		return array;
	}

	private Node parseOptValueList() {
		Node ValueList = new Node(Symbol.OPT_VALUE_LIST);
		Token tok = lexer.peek();
		if (tok.getSymbol() == Symbol.RBRACKET) {
		} else {
			ValueList.getChildren().add(parseValueList());
		}
		return ValueList;
	}

	private Node parseFieldList() {
		Node fieldList = new Node(Symbol.FIELD_LIST);
		//Token tok = lexer.peek();
		fieldList.getChildren().add(parseField());
		Token tok = lexer.peek();
		if (tok.getSymbol() == Symbol.COMMA) {
			fieldList.getChildren().add(expect(Symbol.COMMA));
			fieldList.getChildren().add(parseFieldList());
		}
		return fieldList;
	}

	private Node parseField() {
		Node parseField = new Node(Symbol.FIELD);
		parseField.getChildren().add(expect(Symbol.STRING_LITERAL));
		parseField.getChildren().add(expect(Symbol.COLON));
		parseField.getChildren().add(parseValue());
		return parseField;
	}

	private Node parseValueList() {
		Node valueList = new Node(Symbol.VALUE_LIST);
		//Token tok = lexer.peek();
		valueList.getChildren().add(parseValue());
		Token tok = lexer.peek();
		if (tok.getSymbol() == Symbol.COMMA) {
			valueList.getChildren().add(expect(Symbol.COMMA));
			valueList.getChildren().add(parseValueList());
		}
		return valueList;
	}

	private Node expect(Symbol symbol) {
		Token tok = lexer.next();
		if (tok.getSymbol() != symbol) {
			throw new LexerException("Unexpected token " + tok + " (was expecting " + symbol + ")");
		}
		return new Node(tok);
	}
}
