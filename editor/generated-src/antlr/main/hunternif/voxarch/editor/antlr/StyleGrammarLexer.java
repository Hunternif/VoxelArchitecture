// Generated from StyleGrammar.g4 by ANTLR 4.9.1
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
public class StyleGrammarLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NEWLINE=1, WS=2, INHERIT=3, STR=4, INT=5, FLOAT=6, INT_PCT=7, FLOAT_PCT=8, 
		PLUS=9, MINUS=10, MULT=11, DIV=12, LPAREN=13, RPAREN=14, LBRACKET=15, 
		RBRACKET=16, LBRACE=17, RBRACE=18, LT=19, GT=20, TILDE=21, COLON=22, SEMICOLON=23, 
		DOT=24, COMMA=25, QUOTE=26, CLASSNAME=27, TYPE=28, PROPERTY=29, ENUM=30, 
		COMMENT=31, BLOCK_COMMENT=32;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NEWLINE", "WS", "INHERIT", "STR", "INT", "FLOAT", "INT_PCT", "FLOAT_PCT", 
			"PLUS", "MINUS", "MULT", "DIV", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET", 
			"LBRACE", "RBRACE", "LT", "GT", "TILDE", "COLON", "SEMICOLON", "DOT", 
			"COMMA", "QUOTE", "CLASSNAME", "TYPE", "PROPERTY", "ENUM", "COMMENT", 
			"BLOCK_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'inherit'", null, null, null, null, null, "'+'", "'-'", 
			"'*'", "'/'", "'('", "')'", "'['", "']'", "'{'", "'}'", "'<'", "'>'", 
			"'~'", "':'", "';'", "'.'", "','", "'\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NEWLINE", "WS", "INHERIT", "STR", "INT", "FLOAT", "INT_PCT", "FLOAT_PCT", 
			"PLUS", "MINUS", "MULT", "DIV", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET", 
			"LBRACE", "RBRACE", "LT", "GT", "TILDE", "COLON", "SEMICOLON", "DOT", 
			"COMMA", "QUOTE", "CLASSNAME", "TYPE", "PROPERTY", "ENUM", "COMMENT", 
			"BLOCK_COMMENT"
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


	public StyleGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "StyleGrammar.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\"\u00ec\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\3\2\3\2\5\2G\n\2\3\3\6\3J\n\3\r\3\16\3K\3\3\3\3\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\5\3\5\7\5Z\n\5\f\5\16\5]\13\5\3\5\3\5\3\6\3\6\3\6"+
		"\7\6d\n\6\f\6\16\6g\13\6\5\6i\n\6\3\7\3\7\3\7\7\7n\n\7\f\7\16\7q\13\7"+
		"\3\7\3\7\6\7u\n\7\r\7\16\7v\5\7y\n\7\3\b\3\b\3\b\3\b\7\b\177\n\b\f\b\16"+
		"\b\u0082\13\b\3\b\5\b\u0085\n\b\3\t\3\t\3\t\3\t\7\t\u008b\n\t\f\t\16\t"+
		"\u008e\13\t\3\t\3\t\6\t\u0092\n\t\r\t\16\t\u0093\3\t\5\t\u0097\n\t\3\n"+
		"\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21"+
		"\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\34\6\34\u00be\n\34\r\34\16\34\u00bf\3"+
		"\35\3\35\6\35\u00c4\n\35\r\35\16\35\u00c5\3\36\3\36\7\36\u00ca\n\36\f"+
		"\36\16\36\u00cd\13\36\3\37\6\37\u00d0\n\37\r\37\16\37\u00d1\3 \3 \3 \3"+
		" \7 \u00d8\n \f \16 \u00db\13 \3 \3 \5 \u00df\n \3!\3!\3!\3!\7!\u00e5"+
		"\n!\f!\16!\u00e8\13!\3!\3!\3!\5[\u00d9\u00e6\2\"\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"\3\2\n\4\2\f\f"+
		"\17\17\4\2\13\13\"\"\3\2\63;\3\2\62;\7\2//\62;C\\aac|\3\2C\\\5\2\62;C"+
		"\\c|\3\2c|\2\u00ff\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\3F\3\2\2\2\5I"+
		"\3\2\2\2\7O\3\2\2\2\tW\3\2\2\2\13h\3\2\2\2\rx\3\2\2\2\17\u0084\3\2\2\2"+
		"\21\u0096\3\2\2\2\23\u0098\3\2\2\2\25\u009a\3\2\2\2\27\u009c\3\2\2\2\31"+
		"\u009e\3\2\2\2\33\u00a0\3\2\2\2\35\u00a2\3\2\2\2\37\u00a4\3\2\2\2!\u00a6"+
		"\3\2\2\2#\u00a8\3\2\2\2%\u00aa\3\2\2\2\'\u00ac\3\2\2\2)\u00ae\3\2\2\2"+
		"+\u00b0\3\2\2\2-\u00b2\3\2\2\2/\u00b4\3\2\2\2\61\u00b6\3\2\2\2\63\u00b8"+
		"\3\2\2\2\65\u00ba\3\2\2\2\67\u00bd\3\2\2\29\u00c1\3\2\2\2;\u00c7\3\2\2"+
		"\2=\u00cf\3\2\2\2?\u00d3\3\2\2\2A\u00e0\3\2\2\2CD\7\17\2\2DG\7\f\2\2E"+
		"G\t\2\2\2FC\3\2\2\2FE\3\2\2\2G\4\3\2\2\2HJ\t\3\2\2IH\3\2\2\2JK\3\2\2\2"+
		"KI\3\2\2\2KL\3\2\2\2LM\3\2\2\2MN\b\3\2\2N\6\3\2\2\2OP\7k\2\2PQ\7p\2\2"+
		"QR\7j\2\2RS\7g\2\2ST\7t\2\2TU\7k\2\2UV\7v\2\2V\b\3\2\2\2W[\7$\2\2XZ\n"+
		"\2\2\2YX\3\2\2\2Z]\3\2\2\2[\\\3\2\2\2[Y\3\2\2\2\\^\3\2\2\2][\3\2\2\2^"+
		"_\7$\2\2_\n\3\2\2\2`i\7\62\2\2ae\t\4\2\2bd\t\5\2\2cb\3\2\2\2dg\3\2\2\2"+
		"ec\3\2\2\2ef\3\2\2\2fi\3\2\2\2ge\3\2\2\2h`\3\2\2\2ha\3\2\2\2i\f\3\2\2"+
		"\2jy\7\62\2\2ko\t\4\2\2ln\t\5\2\2ml\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2"+
		"\2\2pr\3\2\2\2qo\3\2\2\2rt\7\60\2\2su\t\5\2\2ts\3\2\2\2uv\3\2\2\2vt\3"+
		"\2\2\2vw\3\2\2\2wy\3\2\2\2xj\3\2\2\2xk\3\2\2\2y\16\3\2\2\2z{\7\62\2\2"+
		"{\u0085\7\'\2\2|\u0080\t\4\2\2}\177\t\5\2\2~}\3\2\2\2\177\u0082\3\2\2"+
		"\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0080"+
		"\3\2\2\2\u0083\u0085\7\'\2\2\u0084z\3\2\2\2\u0084|\3\2\2\2\u0085\20\3"+
		"\2\2\2\u0086\u0087\7\62\2\2\u0087\u0097\7\'\2\2\u0088\u008c\t\4\2\2\u0089"+
		"\u008b\t\5\2\2\u008a\u0089\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2"+
		"\2\2\u008c\u008d\3\2\2\2\u008d\u008f\3\2\2\2\u008e\u008c\3\2\2\2\u008f"+
		"\u0091\7\60\2\2\u0090\u0092\t\5\2\2\u0091\u0090\3\2\2\2\u0092\u0093\3"+
		"\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0095\3\2\2\2\u0095"+
		"\u0097\7\'\2\2\u0096\u0086\3\2\2\2\u0096\u0088\3\2\2\2\u0097\22\3\2\2"+
		"\2\u0098\u0099\7-\2\2\u0099\24\3\2\2\2\u009a\u009b\7/\2\2\u009b\26\3\2"+
		"\2\2\u009c\u009d\7,\2\2\u009d\30\3\2\2\2\u009e\u009f\7\61\2\2\u009f\32"+
		"\3\2\2\2\u00a0\u00a1\7*\2\2\u00a1\34\3\2\2\2\u00a2\u00a3\7+\2\2\u00a3"+
		"\36\3\2\2\2\u00a4\u00a5\7]\2\2\u00a5 \3\2\2\2\u00a6\u00a7\7_\2\2\u00a7"+
		"\"\3\2\2\2\u00a8\u00a9\7}\2\2\u00a9$\3\2\2\2\u00aa\u00ab\7\177\2\2\u00ab"+
		"&\3\2\2\2\u00ac\u00ad\7>\2\2\u00ad(\3\2\2\2\u00ae\u00af\7@\2\2\u00af*"+
		"\3\2\2\2\u00b0\u00b1\7\u0080\2\2\u00b1,\3\2\2\2\u00b2\u00b3\7<\2\2\u00b3"+
		".\3\2\2\2\u00b4\u00b5\7=\2\2\u00b5\60\3\2\2\2\u00b6\u00b7\7\60\2\2\u00b7"+
		"\62\3\2\2\2\u00b8\u00b9\7.\2\2\u00b9\64\3\2\2\2\u00ba\u00bb\7$\2\2\u00bb"+
		"\66\3\2\2\2\u00bc\u00be\t\6\2\2\u00bd\u00bc\3\2\2\2\u00be\u00bf\3\2\2"+
		"\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c08\3\2\2\2\u00c1\u00c3"+
		"\t\7\2\2\u00c2\u00c4\t\b\2\2\u00c3\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5"+
		"\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6:\3\2\2\2\u00c7\u00cb\t\t\2\2"+
		"\u00c8\u00ca\t\6\2\2\u00c9\u00c8\3\2\2\2\u00ca\u00cd\3\2\2\2\u00cb\u00c9"+
		"\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc<\3\2\2\2\u00cd\u00cb\3\2\2\2\u00ce"+
		"\u00d0\t\6\2\2\u00cf\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00cf\3\2"+
		"\2\2\u00d1\u00d2\3\2\2\2\u00d2>\3\2\2\2\u00d3\u00d4\7\61\2\2\u00d4\u00d5"+
		"\7\61\2\2\u00d5\u00d9\3\2\2\2\u00d6\u00d8\13\2\2\2\u00d7\u00d6\3\2\2\2"+
		"\u00d8\u00db\3\2\2\2\u00d9\u00da\3\2\2\2\u00d9\u00d7\3\2\2\2\u00da\u00de"+
		"\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00df\5\3\2\2\u00dd\u00df\7\2\2\3\u00de"+
		"\u00dc\3\2\2\2\u00de\u00dd\3\2\2\2\u00df@\3\2\2\2\u00e0\u00e1\7\61\2\2"+
		"\u00e1\u00e2\7,\2\2\u00e2\u00e6\3\2\2\2\u00e3\u00e5\13\2\2\2\u00e4\u00e3"+
		"\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7"+
		"\u00e9\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00ea\7,\2\2\u00ea\u00eb\7\61"+
		"\2\2\u00ebB\3\2\2\2\27\2FK[ehovx\u0080\u0084\u008c\u0093\u0096\u00bf\u00c5"+
		"\u00cb\u00d1\u00d9\u00de\u00e6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}