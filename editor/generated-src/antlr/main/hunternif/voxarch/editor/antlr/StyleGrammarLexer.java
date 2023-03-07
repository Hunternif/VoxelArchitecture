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
		DOT=24, COMMA=25, SINGLEQUOTE=26, DOUBLEQUOTE=27, ID=28, COMMENT=29, BLOCK_COMMENT=30;
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
			"COMMA", "SINGLEQUOTE", "DOUBLEQUOTE", "ID", "COMMENT", "BLOCK_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'inherit'", null, null, null, null, null, "'+'", "'-'", 
			"'*'", "'/'", "'('", "')'", "'['", "']'", "'{'", "'}'", "'<'", "'>'", 
			"'~'", "':'", "';'", "'.'", "','", "'''", "'\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NEWLINE", "WS", "INHERIT", "STR", "INT", "FLOAT", "INT_PCT", "FLOAT_PCT", 
			"PLUS", "MINUS", "MULT", "DIV", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET", 
			"LBRACE", "RBRACE", "LT", "GT", "TILDE", "COLON", "SEMICOLON", "DOT", 
			"COMMA", "SINGLEQUOTE", "DOUBLEQUOTE", "ID", "COMMENT", "BLOCK_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2 \u00e7\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2\3\2\3"+
		"\2\5\2C\n\2\3\2\3\2\3\3\6\3H\n\3\r\3\16\3I\3\3\3\3\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\5\3\5\7\5X\n\5\f\5\16\5[\13\5\3\5\3\5\3\5\5\5`\n\5\3\5"+
		"\3\5\7\5d\n\5\f\5\16\5g\13\5\3\5\3\5\3\5\5\5l\n\5\5\5n\n\5\3\6\3\6\3\6"+
		"\7\6s\n\6\f\6\16\6v\13\6\5\6x\n\6\3\7\3\7\3\7\7\7}\n\7\f\7\16\7\u0080"+
		"\13\7\3\7\3\7\6\7\u0084\n\7\r\7\16\7\u0085\5\7\u0088\n\7\3\b\3\b\3\b\3"+
		"\b\7\b\u008e\n\b\f\b\16\b\u0091\13\b\3\b\5\b\u0094\n\b\3\t\3\t\3\t\3\t"+
		"\7\t\u009a\n\t\f\t\16\t\u009d\13\t\3\t\3\t\6\t\u00a1\n\t\r\t\16\t\u00a2"+
		"\3\t\5\t\u00a6\n\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3"+
		"\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3"+
		"\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\6"+
		"\35\u00cf\n\35\r\35\16\35\u00d0\3\36\3\36\3\36\3\36\7\36\u00d7\n\36\f"+
		"\36\16\36\u00da\13\36\3\37\3\37\3\37\3\37\7\37\u00e0\n\37\f\37\16\37\u00e3"+
		"\13\37\3\37\3\37\3\37\5Ye\u00e1\2 \3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n"+
		"\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\37= \3\2\t\4\2\f\f\17\17\4\2\13\13\""+
		"\"\3\2))\3\2$$\3\2\63;\3\2\62;\7\2//\62;C\\aac|\2\u00fc\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2"+
		"\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2"+
		"\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2"+
		"\2\3B\3\2\2\2\5G\3\2\2\2\7M\3\2\2\2\tm\3\2\2\2\13w\3\2\2\2\r\u0087\3\2"+
		"\2\2\17\u0093\3\2\2\2\21\u00a5\3\2\2\2\23\u00a7\3\2\2\2\25\u00a9\3\2\2"+
		"\2\27\u00ab\3\2\2\2\31\u00ad\3\2\2\2\33\u00af\3\2\2\2\35\u00b1\3\2\2\2"+
		"\37\u00b3\3\2\2\2!\u00b5\3\2\2\2#\u00b7\3\2\2\2%\u00b9\3\2\2\2\'\u00bb"+
		"\3\2\2\2)\u00bd\3\2\2\2+\u00bf\3\2\2\2-\u00c1\3\2\2\2/\u00c3\3\2\2\2\61"+
		"\u00c5\3\2\2\2\63\u00c7\3\2\2\2\65\u00c9\3\2\2\2\67\u00cb\3\2\2\29\u00ce"+
		"\3\2\2\2;\u00d2\3\2\2\2=\u00db\3\2\2\2?@\7\17\2\2@C\7\f\2\2AC\t\2\2\2"+
		"B?\3\2\2\2BA\3\2\2\2CD\3\2\2\2DE\b\2\2\2E\4\3\2\2\2FH\t\3\2\2GF\3\2\2"+
		"\2HI\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JK\3\2\2\2KL\b\3\2\2L\6\3\2\2\2MN\7k\2"+
		"\2NO\7p\2\2OP\7j\2\2PQ\7g\2\2QR\7t\2\2RS\7k\2\2ST\7v\2\2T\b\3\2\2\2UY"+
		"\5\65\33\2VX\n\4\2\2WV\3\2\2\2X[\3\2\2\2YZ\3\2\2\2YW\3\2\2\2Z_\3\2\2\2"+
		"[Y\3\2\2\2\\`\5\65\33\2]`\5\3\2\2^`\7\2\2\3_\\\3\2\2\2_]\3\2\2\2_^\3\2"+
		"\2\2`n\3\2\2\2ae\5\67\34\2bd\n\5\2\2cb\3\2\2\2dg\3\2\2\2ef\3\2\2\2ec\3"+
		"\2\2\2fk\3\2\2\2ge\3\2\2\2hl\5\67\34\2il\5\3\2\2jl\7\2\2\3kh\3\2\2\2k"+
		"i\3\2\2\2kj\3\2\2\2ln\3\2\2\2mU\3\2\2\2ma\3\2\2\2n\n\3\2\2\2ox\7\62\2"+
		"\2pt\t\6\2\2qs\t\7\2\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2ux\3\2\2"+
		"\2vt\3\2\2\2wo\3\2\2\2wp\3\2\2\2x\f\3\2\2\2y\u0088\7\62\2\2z~\t\6\2\2"+
		"{}\t\7\2\2|{\3\2\2\2}\u0080\3\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\u0081\3"+
		"\2\2\2\u0080~\3\2\2\2\u0081\u0083\7\60\2\2\u0082\u0084\t\7\2\2\u0083\u0082"+
		"\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086"+
		"\u0088\3\2\2\2\u0087y\3\2\2\2\u0087z\3\2\2\2\u0088\16\3\2\2\2\u0089\u008a"+
		"\7\62\2\2\u008a\u0094\7\'\2\2\u008b\u008f\t\6\2\2\u008c\u008e\t\7\2\2"+
		"\u008d\u008c\3\2\2\2\u008e\u0091\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090"+
		"\3\2\2\2\u0090\u0092\3\2\2\2\u0091\u008f\3\2\2\2\u0092\u0094\7\'\2\2\u0093"+
		"\u0089\3\2\2\2\u0093\u008b\3\2\2\2\u0094\20\3\2\2\2\u0095\u0096\7\62\2"+
		"\2\u0096\u00a6\7\'\2\2\u0097\u009b\t\6\2\2\u0098\u009a\t\7\2\2\u0099\u0098"+
		"\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c"+
		"\u009e\3\2\2\2\u009d\u009b\3\2\2\2\u009e\u00a0\7\60\2\2\u009f\u00a1\t"+
		"\7\2\2\u00a0\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2"+
		"\u00a3\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\7\'\2\2\u00a5\u0095\3\2"+
		"\2\2\u00a5\u0097\3\2\2\2\u00a6\22\3\2\2\2\u00a7\u00a8\7-\2\2\u00a8\24"+
		"\3\2\2\2\u00a9\u00aa\7/\2\2\u00aa\26\3\2\2\2\u00ab\u00ac\7,\2\2\u00ac"+
		"\30\3\2\2\2\u00ad\u00ae\7\61\2\2\u00ae\32\3\2\2\2\u00af\u00b0\7*\2\2\u00b0"+
		"\34\3\2\2\2\u00b1\u00b2\7+\2\2\u00b2\36\3\2\2\2\u00b3\u00b4\7]\2\2\u00b4"+
		" \3\2\2\2\u00b5\u00b6\7_\2\2\u00b6\"\3\2\2\2\u00b7\u00b8\7}\2\2\u00b8"+
		"$\3\2\2\2\u00b9\u00ba\7\177\2\2\u00ba&\3\2\2\2\u00bb\u00bc\7>\2\2\u00bc"+
		"(\3\2\2\2\u00bd\u00be\7@\2\2\u00be*\3\2\2\2\u00bf\u00c0\7\u0080\2\2\u00c0"+
		",\3\2\2\2\u00c1\u00c2\7<\2\2\u00c2.\3\2\2\2\u00c3\u00c4\7=\2\2\u00c4\60"+
		"\3\2\2\2\u00c5\u00c6\7\60\2\2\u00c6\62\3\2\2\2\u00c7\u00c8\7.\2\2\u00c8"+
		"\64\3\2\2\2\u00c9\u00ca\7)\2\2\u00ca\66\3\2\2\2\u00cb\u00cc\7$\2\2\u00cc"+
		"8\3\2\2\2\u00cd\u00cf\t\b\2\2\u00ce\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2"+
		"\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1:\3\2\2\2\u00d2\u00d3\7"+
		"\61\2\2\u00d3\u00d4\7\61\2\2\u00d4\u00d8\3\2\2\2\u00d5\u00d7\n\2\2\2\u00d6"+
		"\u00d5\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2"+
		"\2\2\u00d9<\3\2\2\2\u00da\u00d8\3\2\2\2\u00db\u00dc\7\61\2\2\u00dc\u00dd"+
		"\7,\2\2\u00dd\u00e1\3\2\2\2\u00de\u00e0\13\2\2\2\u00df\u00de\3\2\2\2\u00e0"+
		"\u00e3\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e1\u00df\3\2\2\2\u00e2\u00e4\3\2"+
		"\2\2\u00e3\u00e1\3\2\2\2\u00e4\u00e5\7,\2\2\u00e5\u00e6\7\61\2\2\u00e6"+
		">\3\2\2\2\27\2BIY_ekmtw~\u0085\u0087\u008f\u0093\u009b\u00a2\u00a5\u00d0"+
		"\u00d8\u00e1\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}