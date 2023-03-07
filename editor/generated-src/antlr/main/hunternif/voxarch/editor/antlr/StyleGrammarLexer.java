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
		DOT=24, COMMA=25, QUOTE=26, ID=27, COMMENT=28, BLOCK_COMMENT=29;
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
			"COMMA", "QUOTE", "ID", "COMMENT", "BLOCK_COMMENT"
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
			"COMMA", "QUOTE", "ID", "COMMENT", "BLOCK_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\37\u00d4\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\2\5"+
		"\2A\n\2\3\3\6\3D\n\3\r\3\16\3E\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\7\5T\n\5\f\5\16\5W\13\5\3\5\3\5\3\6\3\6\3\6\7\6^\n\6\f\6\16\6"+
		"a\13\6\5\6c\n\6\3\7\3\7\3\7\7\7h\n\7\f\7\16\7k\13\7\3\7\3\7\6\7o\n\7\r"+
		"\7\16\7p\5\7s\n\7\3\b\3\b\3\b\3\b\7\by\n\b\f\b\16\b|\13\b\3\b\5\b\177"+
		"\n\b\3\t\3\t\3\t\3\t\7\t\u0085\n\t\f\t\16\t\u0088\13\t\3\t\3\t\6\t\u008c"+
		"\n\t\r\t\16\t\u008d\3\t\5\t\u0091\n\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3"+
		"\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3"+
		"\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3"+
		"\33\3\34\6\34\u00b8\n\34\r\34\16\34\u00b9\3\35\3\35\3\35\3\35\7\35\u00c0"+
		"\n\35\f\35\16\35\u00c3\13\35\3\35\3\35\5\35\u00c7\n\35\3\36\3\36\3\36"+
		"\3\36\7\36\u00cd\n\36\f\36\16\36\u00d0\13\36\3\36\3\36\3\36\5U\u00c1\u00ce"+
		"\2\37\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35"+
		"\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36"+
		";\37\3\2\7\4\2\f\f\17\17\4\2\13\13\"\"\3\2\63;\3\2\62;\7\2//\62;C\\aa"+
		"c|\2\u00e4\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\3@\3\2\2\2\5C\3\2\2\2\7I\3\2\2\2\tQ\3\2\2\2\13b\3\2\2"+
		"\2\rr\3\2\2\2\17~\3\2\2\2\21\u0090\3\2\2\2\23\u0092\3\2\2\2\25\u0094\3"+
		"\2\2\2\27\u0096\3\2\2\2\31\u0098\3\2\2\2\33\u009a\3\2\2\2\35\u009c\3\2"+
		"\2\2\37\u009e\3\2\2\2!\u00a0\3\2\2\2#\u00a2\3\2\2\2%\u00a4\3\2\2\2\'\u00a6"+
		"\3\2\2\2)\u00a8\3\2\2\2+\u00aa\3\2\2\2-\u00ac\3\2\2\2/\u00ae\3\2\2\2\61"+
		"\u00b0\3\2\2\2\63\u00b2\3\2\2\2\65\u00b4\3\2\2\2\67\u00b7\3\2\2\29\u00bb"+
		"\3\2\2\2;\u00c8\3\2\2\2=>\7\17\2\2>A\7\f\2\2?A\t\2\2\2@=\3\2\2\2@?\3\2"+
		"\2\2A\4\3\2\2\2BD\t\3\2\2CB\3\2\2\2DE\3\2\2\2EC\3\2\2\2EF\3\2\2\2FG\3"+
		"\2\2\2GH\b\3\2\2H\6\3\2\2\2IJ\7k\2\2JK\7p\2\2KL\7j\2\2LM\7g\2\2MN\7t\2"+
		"\2NO\7k\2\2OP\7v\2\2P\b\3\2\2\2QU\7$\2\2RT\n\2\2\2SR\3\2\2\2TW\3\2\2\2"+
		"UV\3\2\2\2US\3\2\2\2VX\3\2\2\2WU\3\2\2\2XY\7$\2\2Y\n\3\2\2\2Zc\7\62\2"+
		"\2[_\t\4\2\2\\^\t\5\2\2]\\\3\2\2\2^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`c\3\2"+
		"\2\2a_\3\2\2\2bZ\3\2\2\2b[\3\2\2\2c\f\3\2\2\2ds\7\62\2\2ei\t\4\2\2fh\t"+
		"\5\2\2gf\3\2\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2\2\2jl\3\2\2\2ki\3\2\2\2ln\7"+
		"\60\2\2mo\t\5\2\2nm\3\2\2\2op\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rd"+
		"\3\2\2\2re\3\2\2\2s\16\3\2\2\2tu\7\62\2\2u\177\7\'\2\2vz\t\4\2\2wy\t\5"+
		"\2\2xw\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{}\3\2\2\2|z\3\2\2\2}\177"+
		"\7\'\2\2~t\3\2\2\2~v\3\2\2\2\177\20\3\2\2\2\u0080\u0081\7\62\2\2\u0081"+
		"\u0091\7\'\2\2\u0082\u0086\t\4\2\2\u0083\u0085\t\5\2\2\u0084\u0083\3\2"+
		"\2\2\u0085\u0088\3\2\2\2\u0086\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087"+
		"\u0089\3\2\2\2\u0088\u0086\3\2\2\2\u0089\u008b\7\60\2\2\u008a\u008c\t"+
		"\5\2\2\u008b\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008b\3\2\2\2\u008d"+
		"\u008e\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091\7\'\2\2\u0090\u0080\3\2"+
		"\2\2\u0090\u0082\3\2\2\2\u0091\22\3\2\2\2\u0092\u0093\7-\2\2\u0093\24"+
		"\3\2\2\2\u0094\u0095\7/\2\2\u0095\26\3\2\2\2\u0096\u0097\7,\2\2\u0097"+
		"\30\3\2\2\2\u0098\u0099\7\61\2\2\u0099\32\3\2\2\2\u009a\u009b\7*\2\2\u009b"+
		"\34\3\2\2\2\u009c\u009d\7+\2\2\u009d\36\3\2\2\2\u009e\u009f\7]\2\2\u009f"+
		" \3\2\2\2\u00a0\u00a1\7_\2\2\u00a1\"\3\2\2\2\u00a2\u00a3\7}\2\2\u00a3"+
		"$\3\2\2\2\u00a4\u00a5\7\177\2\2\u00a5&\3\2\2\2\u00a6\u00a7\7>\2\2\u00a7"+
		"(\3\2\2\2\u00a8\u00a9\7@\2\2\u00a9*\3\2\2\2\u00aa\u00ab\7\u0080\2\2\u00ab"+
		",\3\2\2\2\u00ac\u00ad\7<\2\2\u00ad.\3\2\2\2\u00ae\u00af\7=\2\2\u00af\60"+
		"\3\2\2\2\u00b0\u00b1\7\60\2\2\u00b1\62\3\2\2\2\u00b2\u00b3\7.\2\2\u00b3"+
		"\64\3\2\2\2\u00b4\u00b5\7$\2\2\u00b5\66\3\2\2\2\u00b6\u00b8\t\6\2\2\u00b7"+
		"\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2"+
		"\2\2\u00ba8\3\2\2\2\u00bb\u00bc\7\61\2\2\u00bc\u00bd\7\61\2\2\u00bd\u00c1"+
		"\3\2\2\2\u00be\u00c0\13\2\2\2\u00bf\u00be\3\2\2\2\u00c0\u00c3\3\2\2\2"+
		"\u00c1\u00c2\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2\u00c6\3\2\2\2\u00c3\u00c1"+
		"\3\2\2\2\u00c4\u00c7\5\3\2\2\u00c5\u00c7\7\2\2\3\u00c6\u00c4\3\2\2\2\u00c6"+
		"\u00c5\3\2\2\2\u00c7:\3\2\2\2\u00c8\u00c9\7\61\2\2\u00c9\u00ca\7,\2\2"+
		"\u00ca\u00ce\3\2\2\2\u00cb\u00cd\13\2\2\2\u00cc\u00cb\3\2\2\2\u00cd\u00d0"+
		"\3\2\2\2\u00ce\u00cf\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d1\3\2\2\2\u00d0"+
		"\u00ce\3\2\2\2\u00d1\u00d2\7,\2\2\u00d2\u00d3\7\61\2\2\u00d3<\3\2\2\2"+
		"\24\2@EU_biprz~\u0086\u008d\u0090\u00b9\u00c1\u00c6\u00ce\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}