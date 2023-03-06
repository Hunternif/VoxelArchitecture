// Generated from StyleLexer.g4 by ANTLR 4.9.1
package hunternif.voxarch.editor.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StyleLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NEWLINE=1, WS=2, INHERIT=3, STR=4, INT=5, FLOAT=6, INT_PCT=7, FLOAT_PCT=8, 
		PLUS=9, MINUS=10, ASTERISK=11, DIVISION=12, LPAREN=13, RPAREN=14, LBRACKET=15, 
		RBRACKET=16, LBRACE=17, RBRACE=18, LT=19, GT=20, TILDE=21, COLON=22, SEMICOLON=23, 
		CLASSNAME=24, TYPE=25, PROPERTY=26;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NEWLINE", "WS", "INHERIT", "STR", "INT", "FLOAT", "INT_PCT", "FLOAT_PCT", 
			"PLUS", "MINUS", "ASTERISK", "DIVISION", "LPAREN", "RPAREN", "LBRACKET", 
			"RBRACKET", "LBRACE", "RBRACE", "LT", "GT", "TILDE", "COLON", "SEMICOLON", 
			"CLASSNAME", "TYPE", "PROPERTY"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'inherit'", null, null, null, null, null, "'+'", "'-'", 
			"'*'", "'/'", "'('", "')'", "'['", "']'", "'{'", "'}'", "'<'", "'>'", 
			"'~'", "':'", "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NEWLINE", "WS", "INHERIT", "STR", "INT", "FLOAT", "INT_PCT", "FLOAT_PCT", 
			"PLUS", "MINUS", "ASTERISK", "DIVISION", "LPAREN", "RPAREN", "LBRACKET", 
			"RBRACKET", "LBRACE", "RBRACE", "LT", "GT", "TILDE", "COLON", "SEMICOLON", 
			"CLASSNAME", "TYPE", "PROPERTY"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public StyleLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "StyleLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\34\u00bc\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\2\5\2;\n\2\3\3\6\3>\n\3\r\3\16\3?"+
		"\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\7\5N\n\5\f\5\16\5Q\13"+
		"\5\3\5\3\5\3\6\3\6\3\6\7\6X\n\6\f\6\16\6[\13\6\5\6]\n\6\3\7\3\7\3\7\7"+
		"\7b\n\7\f\7\16\7e\13\7\3\7\3\7\6\7i\n\7\r\7\16\7j\5\7m\n\7\3\b\3\b\3\b"+
		"\3\b\7\bs\n\b\f\b\16\bv\13\b\3\b\5\by\n\b\3\t\3\t\3\t\3\t\7\t\177\n\t"+
		"\f\t\16\t\u0082\13\t\3\t\3\t\6\t\u0086\n\t\r\t\16\t\u0087\3\t\5\t\u008b"+
		"\n\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3"+
		"\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3"+
		"\30\3\30\3\31\6\31\u00ac\n\31\r\31\16\31\u00ad\3\32\3\32\6\32\u00b2\n"+
		"\32\r\32\16\32\u00b3\3\33\3\33\7\33\u00b8\n\33\f\33\16\33\u00bb\13\33"+
		"\3O\2\34\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17"+
		"\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\3\2\n"+
		"\4\2\f\ftt\4\2\13\13\"\"\3\2\63;\3\2\62;\7\2//\62;C\\aac|\3\2C\\\5\2\62"+
		";C\\c|\3\2c|\2\u00cb\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"+
		"\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2"+
		"\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\3:\3\2\2"+
		"\2\5=\3\2\2\2\7C\3\2\2\2\tK\3\2\2\2\13\\\3\2\2\2\rl\3\2\2\2\17x\3\2\2"+
		"\2\21\u008a\3\2\2\2\23\u008c\3\2\2\2\25\u008e\3\2\2\2\27\u0090\3\2\2\2"+
		"\31\u0092\3\2\2\2\33\u0094\3\2\2\2\35\u0096\3\2\2\2\37\u0098\3\2\2\2!"+
		"\u009a\3\2\2\2#\u009c\3\2\2\2%\u009e\3\2\2\2\'\u00a0\3\2\2\2)\u00a2\3"+
		"\2\2\2+\u00a4\3\2\2\2-\u00a6\3\2\2\2/\u00a8\3\2\2\2\61\u00ab\3\2\2\2\63"+
		"\u00af\3\2\2\2\65\u00b5\3\2\2\2\678\7\17\2\28;\7\f\2\29;\t\2\2\2:\67\3"+
		"\2\2\2:9\3\2\2\2;\4\3\2\2\2<>\t\3\2\2=<\3\2\2\2>?\3\2\2\2?=\3\2\2\2?@"+
		"\3\2\2\2@A\3\2\2\2AB\b\3\2\2B\6\3\2\2\2CD\7k\2\2DE\7p\2\2EF\7j\2\2FG\7"+
		"g\2\2GH\7t\2\2HI\7k\2\2IJ\7v\2\2J\b\3\2\2\2KO\7$\2\2LN\13\2\2\2ML\3\2"+
		"\2\2NQ\3\2\2\2OP\3\2\2\2OM\3\2\2\2PR\3\2\2\2QO\3\2\2\2RS\7$\2\2S\n\3\2"+
		"\2\2T]\7\62\2\2UY\t\4\2\2VX\t\5\2\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3"+
		"\2\2\2Z]\3\2\2\2[Y\3\2\2\2\\T\3\2\2\2\\U\3\2\2\2]\f\3\2\2\2^m\7\62\2\2"+
		"_c\t\4\2\2`b\t\5\2\2a`\3\2\2\2be\3\2\2\2ca\3\2\2\2cd\3\2\2\2df\3\2\2\2"+
		"ec\3\2\2\2fh\7\60\2\2gi\t\5\2\2hg\3\2\2\2ij\3\2\2\2jh\3\2\2\2jk\3\2\2"+
		"\2km\3\2\2\2l^\3\2\2\2l_\3\2\2\2m\16\3\2\2\2no\7\62\2\2oy\7\'\2\2pt\t"+
		"\4\2\2qs\t\5\2\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2uw\3\2\2\2vt\3"+
		"\2\2\2wy\7\'\2\2xn\3\2\2\2xp\3\2\2\2y\20\3\2\2\2z{\7\62\2\2{\u008b\7\'"+
		"\2\2|\u0080\t\4\2\2}\177\t\5\2\2~}\3\2\2\2\177\u0082\3\2\2\2\u0080~\3"+
		"\2\2\2\u0080\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0080\3\2\2\2\u0083"+
		"\u0085\7\60\2\2\u0084\u0086\t\5\2\2\u0085\u0084\3\2\2\2\u0086\u0087\3"+
		"\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\u008b\7\'\2\2\u008az\3\2\2\2\u008a|\3\2\2\2\u008b\22\3\2\2\2\u008c\u008d"+
		"\7-\2\2\u008d\24\3\2\2\2\u008e\u008f\7/\2\2\u008f\26\3\2\2\2\u0090\u0091"+
		"\7,\2\2\u0091\30\3\2\2\2\u0092\u0093\7\61\2\2\u0093\32\3\2\2\2\u0094\u0095"+
		"\7*\2\2\u0095\34\3\2\2\2\u0096\u0097\7+\2\2\u0097\36\3\2\2\2\u0098\u0099"+
		"\7]\2\2\u0099 \3\2\2\2\u009a\u009b\7_\2\2\u009b\"\3\2\2\2\u009c\u009d"+
		"\7}\2\2\u009d$\3\2\2\2\u009e\u009f\7\177\2\2\u009f&\3\2\2\2\u00a0\u00a1"+
		"\7>\2\2\u00a1(\3\2\2\2\u00a2\u00a3\7@\2\2\u00a3*\3\2\2\2\u00a4\u00a5\7"+
		"\u0080\2\2\u00a5,\3\2\2\2\u00a6\u00a7\7<\2\2\u00a7.\3\2\2\2\u00a8\u00a9"+
		"\7=\2\2\u00a9\60\3\2\2\2\u00aa\u00ac\t\6\2\2\u00ab\u00aa\3\2\2\2\u00ac"+
		"\u00ad\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\62\3\2\2"+
		"\2\u00af\u00b1\t\7\2\2\u00b0\u00b2\t\b\2\2\u00b1\u00b0\3\2\2\2\u00b2\u00b3"+
		"\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\64\3\2\2\2\u00b5"+
		"\u00b9\t\t\2\2\u00b6\u00b8\t\6\2\2\u00b7\u00b6\3\2\2\2\u00b8\u00bb\3\2"+
		"\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\66\3\2\2\2\u00bb\u00b9"+
		"\3\2\2\2\23\2:?OY\\cjltx\u0080\u0087\u008a\u00ad\u00b3\u00b9\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}