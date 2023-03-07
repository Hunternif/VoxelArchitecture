// Generated from StyleGrammar.g4 by ANTLR 4.9.1
package hunternif.voxarch.editor.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StyleGrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NEWLINE=1, WS=2, INHERIT=3, STR=4, INT=5, FLOAT=6, INT_PCT=7, FLOAT_PCT=8, 
		PLUS=9, MINUS=10, MULT=11, DIV=12, LPAREN=13, RPAREN=14, LBRACKET=15, 
		RBRACKET=16, LBRACE=17, RBRACE=18, LT=19, GT=20, TILDE=21, COLON=22, SEMICOLON=23, 
		DOT=24, COMMA=25, SINGLEQUOTE=26, DOUBLEQUOTE=27, ID=28, COMMENT=29, BLOCK_COMMENT=30;
	public static final int
		RULE_stylesheet = 0, RULE_styleRule = 1, RULE_ruleBody = 2, RULE_declaration = 3, 
		RULE_propValue = 4, RULE_selector = 5, RULE_dotClass = 6, RULE_numExpression = 7, 
		RULE_dimExpression = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"stylesheet", "styleRule", "ruleBody", "declaration", "propValue", "selector", 
			"dotClass", "numExpression", "dimExpression"
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

	@Override
	public String getGrammarFileName() { return "StyleGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public StyleGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class StylesheetContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(StyleGrammarParser.EOF, 0); }
		public List<StyleRuleContext> styleRule() {
			return getRuleContexts(StyleRuleContext.class);
		}
		public StyleRuleContext styleRule(int i) {
			return getRuleContext(StyleRuleContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(StyleGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(StyleGrammarParser.NEWLINE, i);
		}
		public StylesheetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stylesheet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterStylesheet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitStylesheet(this);
		}
	}

	public final StylesheetContext stylesheet() throws RecognitionException {
		StylesheetContext _localctx = new StylesheetContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_stylesheet);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACKET) | (1L << DOT) | (1L << ID))) != 0)) {
				{
				{
				setState(18);
				styleRule();
				setState(22);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NEWLINE) {
					{
					{
					setState(19);
					match(NEWLINE);
					}
					}
					setState(24);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(30);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StyleRuleContext extends ParserRuleContext {
		public SelectorContext selector() {
			return getRuleContext(SelectorContext.class,0);
		}
		public TerminalNode LBRACE() { return getToken(StyleGrammarParser.LBRACE, 0); }
		public RuleBodyContext ruleBody() {
			return getRuleContext(RuleBodyContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(StyleGrammarParser.RBRACE, 0); }
		public List<TerminalNode> NEWLINE() { return getTokens(StyleGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(StyleGrammarParser.NEWLINE, i);
		}
		public StyleRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_styleRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterStyleRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitStyleRule(this);
		}
	}

	public final StyleRuleContext styleRule() throws RecognitionException {
		StyleRuleContext _localctx = new StyleRuleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_styleRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			selector(0);
			setState(33);
			match(LBRACE);
			setState(37);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(34);
				match(NEWLINE);
				}
				}
				setState(39);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(40);
			ruleBody();
			setState(41);
			match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleBodyContext extends ParserRuleContext {
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public RuleBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterRuleBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitRuleBody(this);
		}
	}

	public final RuleBodyContext ruleBody() throws RecognitionException {
		RuleBodyContext _localctx = new RuleBodyContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_ruleBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(43);
				declaration();
				}
				}
				setState(46); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationContext extends ParserRuleContext {
		public Token property;
		public PropValueContext value;
		public TerminalNode COLON() { return getToken(StyleGrammarParser.COLON, 0); }
		public TerminalNode ID() { return getToken(StyleGrammarParser.ID, 0); }
		public PropValueContext propValue() {
			return getRuleContext(PropValueContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(StyleGrammarParser.SEMICOLON, 0); }
		public List<TerminalNode> NEWLINE() { return getTokens(StyleGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(StyleGrammarParser.NEWLINE, i);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitDeclaration(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			((DeclarationContext)_localctx).property = match(ID);
			setState(49);
			match(COLON);
			setState(50);
			((DeclarationContext)_localctx).value = propValue();
			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(51);
				match(SEMICOLON);
				}
			}

			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(54);
				match(NEWLINE);
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropValueContext extends ParserRuleContext {
		public PropValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propValue; }
	 
		public PropValueContext() { }
		public void copyFrom(PropValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DimValueContext extends PropValueContext {
		public DimExpressionContext dimExpression() {
			return getRuleContext(DimExpressionContext.class,0);
		}
		public DimValueContext(PropValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterDimValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitDimValue(this);
		}
	}
	public static class EnumValueContext extends PropValueContext {
		public TerminalNode ID() { return getToken(StyleGrammarParser.ID, 0); }
		public EnumValueContext(PropValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterEnumValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitEnumValue(this);
		}
	}
	public static class StrValueContext extends PropValueContext {
		public TerminalNode STR() { return getToken(StyleGrammarParser.STR, 0); }
		public StrValueContext(PropValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterStrValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitStrValue(this);
		}
	}
	public static class NumValueContext extends PropValueContext {
		public NumExpressionContext numExpression() {
			return getRuleContext(NumExpressionContext.class,0);
		}
		public NumValueContext(PropValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterNumValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitNumValue(this);
		}
	}

	public final PropValueContext propValue() throws RecognitionException {
		PropValueContext _localctx = new PropValueContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_propValue);
		try {
			setState(64);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new DimValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(60);
				dimExpression(0);
				}
				break;
			case 2:
				_localctx = new NumValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(61);
				numExpression(0);
				}
				break;
			case 3:
				_localctx = new EnumValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(62);
				match(ID);
				}
				break;
			case 4:
				_localctx = new StrValueContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(63);
				match(STR);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectorContext extends ParserRuleContext {
		public SelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selector; }
	 
		public SelectorContext() { }
		public void copyFrom(SelectorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DescendantSelectorContext extends SelectorContext {
		public TerminalNode LBRACKET() { return getToken(StyleGrammarParser.LBRACKET, 0); }
		public List<SelectorContext> selector() {
			return getRuleContexts(SelectorContext.class);
		}
		public SelectorContext selector(int i) {
			return getRuleContext(SelectorContext.class,i);
		}
		public TerminalNode RBRACKET() { return getToken(StyleGrammarParser.RBRACKET, 0); }
		public DescendantSelectorContext(SelectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterDescendantSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitDescendantSelector(this);
		}
	}
	public static class TypeSelectorContext extends SelectorContext {
		public TerminalNode ID() { return getToken(StyleGrammarParser.ID, 0); }
		public TypeSelectorContext(SelectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterTypeSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitTypeSelector(this);
		}
	}
	public static class ChildSelectorContext extends SelectorContext {
		public List<SelectorContext> selector() {
			return getRuleContexts(SelectorContext.class);
		}
		public SelectorContext selector(int i) {
			return getRuleContext(SelectorContext.class,i);
		}
		public TerminalNode GT() { return getToken(StyleGrammarParser.GT, 0); }
		public ChildSelectorContext(SelectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterChildSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitChildSelector(this);
		}
	}
	public static class ListSelectorContext extends SelectorContext {
		public List<SelectorContext> selector() {
			return getRuleContexts(SelectorContext.class);
		}
		public SelectorContext selector(int i) {
			return getRuleContext(SelectorContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(StyleGrammarParser.COMMA, 0); }
		public ListSelectorContext(SelectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterListSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitListSelector(this);
		}
	}
	public static class AndSelectorContext extends SelectorContext {
		public SelectorContext selector() {
			return getRuleContext(SelectorContext.class,0);
		}
		public DotClassContext dotClass() {
			return getRuleContext(DotClassContext.class,0);
		}
		public AndSelectorContext(SelectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterAndSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitAndSelector(this);
		}
	}
	public static class ClassSelectorContext extends SelectorContext {
		public DotClassContext dotClass() {
			return getRuleContext(DotClassContext.class,0);
		}
		public ClassSelectorContext(SelectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterClassSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitClassSelector(this);
		}
	}

	public final SelectorContext selector() throws RecognitionException {
		return selector(0);
	}

	private SelectorContext selector(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		SelectorContext _localctx = new SelectorContext(_ctx, _parentState);
		SelectorContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_selector, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACKET:
				{
				_localctx = new DescendantSelectorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(67);
				match(LBRACKET);
				setState(68);
				selector(0);
				setState(69);
				match(RBRACKET);
				setState(70);
				selector(3);
				}
				break;
			case DOT:
				{
				_localctx = new ClassSelectorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(72);
				dotClass();
				}
				break;
			case ID:
				{
				_localctx = new TypeSelectorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(73);
				match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(86);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(84);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						_localctx = new ListSelectorContext(new SelectorContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(76);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(77);
						match(COMMA);
						setState(78);
						selector(7);
						}
						break;
					case 2:
						{
						_localctx = new ChildSelectorContext(new SelectorContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(79);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(80);
						match(GT);
						setState(81);
						selector(5);
						}
						break;
					case 3:
						{
						_localctx = new AndSelectorContext(new SelectorContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(82);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(83);
						dotClass();
						}
						break;
					}
					} 
				}
				setState(88);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class DotClassContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(StyleGrammarParser.DOT, 0); }
		public TerminalNode ID() { return getToken(StyleGrammarParser.ID, 0); }
		public DotClassContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dotClass; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterDotClass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitDotClass(this);
		}
	}

	public final DotClassContext dotClass() throws RecognitionException {
		DotClassContext _localctx = new DotClassContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_dotClass);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(DOT);
			setState(90);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumExpressionContext extends ParserRuleContext {
		public NumExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numExpression; }
	 
		public NumExpressionContext() { }
		public void copyFrom(NumExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NumParenExpressionContext extends NumExpressionContext {
		public TerminalNode LPAREN() { return getToken(StyleGrammarParser.LPAREN, 0); }
		public NumExpressionContext numExpression() {
			return getRuleContext(NumExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(StyleGrammarParser.RPAREN, 0); }
		public NumParenExpressionContext(NumExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterNumParenExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitNumParenExpression(this);
		}
	}
	public static class NumMinusExpressionContext extends NumExpressionContext {
		public TerminalNode MINUS() { return getToken(StyleGrammarParser.MINUS, 0); }
		public NumExpressionContext numExpression() {
			return getRuleContext(NumExpressionContext.class,0);
		}
		public NumMinusExpressionContext(NumExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterNumMinusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitNumMinusExpression(this);
		}
	}
	public static class IntLiteralContext extends NumExpressionContext {
		public TerminalNode INT() { return getToken(StyleGrammarParser.INT, 0); }
		public IntLiteralContext(NumExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterIntLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitIntLiteral(this);
		}
	}
	public static class FloatLiteralContext extends NumExpressionContext {
		public TerminalNode FLOAT() { return getToken(StyleGrammarParser.FLOAT, 0); }
		public FloatLiteralContext(NumExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterFloatLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitFloatLiteral(this);
		}
	}
	public static class NumBinaryOperationContext extends NumExpressionContext {
		public NumExpressionContext left;
		public Token op;
		public NumExpressionContext right;
		public List<NumExpressionContext> numExpression() {
			return getRuleContexts(NumExpressionContext.class);
		}
		public NumExpressionContext numExpression(int i) {
			return getRuleContext(NumExpressionContext.class,i);
		}
		public TerminalNode DIV() { return getToken(StyleGrammarParser.DIV, 0); }
		public TerminalNode MULT() { return getToken(StyleGrammarParser.MULT, 0); }
		public TerminalNode PLUS() { return getToken(StyleGrammarParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(StyleGrammarParser.MINUS, 0); }
		public NumBinaryOperationContext(NumExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterNumBinaryOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitNumBinaryOperation(this);
		}
	}

	public final NumExpressionContext numExpression() throws RecognitionException {
		return numExpression(0);
	}

	private NumExpressionContext numExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		NumExpressionContext _localctx = new NumExpressionContext(_ctx, _parentState);
		NumExpressionContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_numExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new NumParenExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(93);
				match(LPAREN);
				setState(94);
				numExpression(0);
				setState(95);
				match(RPAREN);
				}
				break;
			case MINUS:
				{
				_localctx = new NumMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97);
				match(MINUS);
				setState(98);
				numExpression(3);
				}
				break;
			case INT:
				{
				_localctx = new IntLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(99);
				match(INT);
				}
				break;
			case FLOAT:
				{
				_localctx = new FloatLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(100);
				match(FLOAT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(111);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(109);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
					case 1:
						{
						_localctx = new NumBinaryOperationContext(new NumExpressionContext(_parentctx, _parentState));
						((NumBinaryOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_numExpression);
						setState(103);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(104);
						((NumBinaryOperationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MULT || _la==DIV) ) {
							((NumBinaryOperationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(105);
						((NumBinaryOperationContext)_localctx).right = numExpression(7);
						}
						break;
					case 2:
						{
						_localctx = new NumBinaryOperationContext(new NumExpressionContext(_parentctx, _parentState));
						((NumBinaryOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_numExpression);
						setState(106);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(107);
						((NumBinaryOperationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((NumBinaryOperationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(108);
						((NumBinaryOperationContext)_localctx).right = numExpression(6);
						}
						break;
					}
					} 
				}
				setState(113);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class DimExpressionContext extends ParserRuleContext {
		public DimExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dimExpression; }
	 
		public DimExpressionContext() { }
		public void copyFrom(DimExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IntPctLiteralContext extends DimExpressionContext {
		public TerminalNode INT_PCT() { return getToken(StyleGrammarParser.INT_PCT, 0); }
		public IntPctLiteralContext(DimExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterIntPctLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitIntPctLiteral(this);
		}
	}
	public static class FloatPctLiteralContext extends DimExpressionContext {
		public TerminalNode FLOAT_PCT() { return getToken(StyleGrammarParser.FLOAT_PCT, 0); }
		public FloatPctLiteralContext(DimExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterFloatPctLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitFloatPctLiteral(this);
		}
	}
	public static class DimAddOperationContext extends DimExpressionContext {
		public DimExpressionContext left;
		public Token op;
		public DimExpressionContext right;
		public List<DimExpressionContext> dimExpression() {
			return getRuleContexts(DimExpressionContext.class);
		}
		public DimExpressionContext dimExpression(int i) {
			return getRuleContext(DimExpressionContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(StyleGrammarParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(StyleGrammarParser.MINUS, 0); }
		public DimAddOperationContext(DimExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterDimAddOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitDimAddOperation(this);
		}
	}
	public static class NumberAsDimContext extends DimExpressionContext {
		public NumExpressionContext numExpression() {
			return getRuleContext(NumExpressionContext.class,0);
		}
		public NumberAsDimContext(DimExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterNumberAsDim(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitNumberAsDim(this);
		}
	}
	public static class DimMultOperationContext extends DimExpressionContext {
		public DimExpressionContext left;
		public Token op;
		public NumExpressionContext right;
		public DimExpressionContext dimExpression() {
			return getRuleContext(DimExpressionContext.class,0);
		}
		public NumExpressionContext numExpression() {
			return getRuleContext(NumExpressionContext.class,0);
		}
		public TerminalNode DIV() { return getToken(StyleGrammarParser.DIV, 0); }
		public TerminalNode MULT() { return getToken(StyleGrammarParser.MULT, 0); }
		public DimMultOperationContext(DimExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterDimMultOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitDimMultOperation(this);
		}
	}

	public final DimExpressionContext dimExpression() throws RecognitionException {
		return dimExpression(0);
	}

	private DimExpressionContext dimExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		DimExpressionContext _localctx = new DimExpressionContext(_ctx, _parentState);
		DimExpressionContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_dimExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case MINUS:
			case LPAREN:
				{
				_localctx = new NumberAsDimContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(115);
				numExpression(0);
				}
				break;
			case INT_PCT:
				{
				_localctx = new IntPctLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(116);
				match(INT_PCT);
				}
				break;
			case FLOAT_PCT:
				{
				_localctx = new FloatPctLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(117);
				match(FLOAT_PCT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(128);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(126);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new DimAddOperationContext(new DimExpressionContext(_parentctx, _parentState));
						((DimAddOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dimExpression);
						setState(120);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(121);
						((DimAddOperationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((DimAddOperationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(122);
						((DimAddOperationContext)_localctx).right = dimExpression(5);
						}
						break;
					case 2:
						{
						_localctx = new DimMultOperationContext(new DimExpressionContext(_parentctx, _parentState));
						((DimMultOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dimExpression);
						setState(123);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(124);
						((DimMultOperationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MULT || _la==DIV) ) {
							((DimMultOperationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(125);
						((DimMultOperationContext)_localctx).right = numExpression(0);
						}
						break;
					}
					} 
				}
				setState(130);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 5:
			return selector_sempred((SelectorContext)_localctx, predIndex);
		case 7:
			return numExpression_sempred((NumExpressionContext)_localctx, predIndex);
		case 8:
			return dimExpression_sempred((DimExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean selector_sempred(SelectorContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 4);
		case 2:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean numExpression_sempred(NumExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean dimExpression_sempred(DimExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 5);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3 \u0086\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2"+
		"\7\2\27\n\2\f\2\16\2\32\13\2\7\2\34\n\2\f\2\16\2\37\13\2\3\2\3\2\3\3\3"+
		"\3\3\3\7\3&\n\3\f\3\16\3)\13\3\3\3\3\3\3\3\3\4\6\4/\n\4\r\4\16\4\60\3"+
		"\5\3\5\3\5\3\5\5\5\67\n\5\3\5\7\5:\n\5\f\5\16\5=\13\5\3\6\3\6\3\6\3\6"+
		"\5\6C\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7M\n\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\7\7W\n\7\f\7\16\7Z\13\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\5\th\n\t\3\t\3\t\3\t\3\t\3\t\3\t\7\tp\n\t\f\t\16\ts\13"+
		"\t\3\n\3\n\3\n\3\n\5\ny\n\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0081\n\n\f\n"+
		"\16\n\u0084\13\n\3\n\2\5\f\20\22\13\2\4\6\b\n\f\16\20\22\2\4\3\2\r\16"+
		"\3\2\13\f\2\u0093\2\35\3\2\2\2\4\"\3\2\2\2\6.\3\2\2\2\b\62\3\2\2\2\nB"+
		"\3\2\2\2\fL\3\2\2\2\16[\3\2\2\2\20g\3\2\2\2\22x\3\2\2\2\24\30\5\4\3\2"+
		"\25\27\7\3\2\2\26\25\3\2\2\2\27\32\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2"+
		"\31\34\3\2\2\2\32\30\3\2\2\2\33\24\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2"+
		"\35\36\3\2\2\2\36 \3\2\2\2\37\35\3\2\2\2 !\7\2\2\3!\3\3\2\2\2\"#\5\f\7"+
		"\2#\'\7\23\2\2$&\7\3\2\2%$\3\2\2\2&)\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2(*\3"+
		"\2\2\2)\'\3\2\2\2*+\5\6\4\2+,\7\24\2\2,\5\3\2\2\2-/\5\b\5\2.-\3\2\2\2"+
		"/\60\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\7\3\2\2\2\62\63\7\36\2\2\63"+
		"\64\7\30\2\2\64\66\5\n\6\2\65\67\7\31\2\2\66\65\3\2\2\2\66\67\3\2\2\2"+
		"\67;\3\2\2\28:\7\3\2\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<\t\3\2"+
		"\2\2=;\3\2\2\2>C\5\22\n\2?C\5\20\t\2@C\7\36\2\2AC\7\6\2\2B>\3\2\2\2B?"+
		"\3\2\2\2B@\3\2\2\2BA\3\2\2\2C\13\3\2\2\2DE\b\7\1\2EF\7\21\2\2FG\5\f\7"+
		"\2GH\7\22\2\2HI\5\f\7\5IM\3\2\2\2JM\5\16\b\2KM\7\36\2\2LD\3\2\2\2LJ\3"+
		"\2\2\2LK\3\2\2\2MX\3\2\2\2NO\f\b\2\2OP\7\33\2\2PW\5\f\7\tQR\f\6\2\2RS"+
		"\7\26\2\2SW\5\f\7\7TU\f\7\2\2UW\5\16\b\2VN\3\2\2\2VQ\3\2\2\2VT\3\2\2\2"+
		"WZ\3\2\2\2XV\3\2\2\2XY\3\2\2\2Y\r\3\2\2\2ZX\3\2\2\2[\\\7\32\2\2\\]\7\36"+
		"\2\2]\17\3\2\2\2^_\b\t\1\2_`\7\17\2\2`a\5\20\t\2ab\7\20\2\2bh\3\2\2\2"+
		"cd\7\f\2\2dh\5\20\t\5eh\7\7\2\2fh\7\b\2\2g^\3\2\2\2gc\3\2\2\2ge\3\2\2"+
		"\2gf\3\2\2\2hq\3\2\2\2ij\f\b\2\2jk\t\2\2\2kp\5\20\t\tlm\f\7\2\2mn\t\3"+
		"\2\2np\5\20\t\boi\3\2\2\2ol\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2\2\2r\21"+
		"\3\2\2\2sq\3\2\2\2tu\b\n\1\2uy\5\20\t\2vy\7\t\2\2wy\7\n\2\2xt\3\2\2\2"+
		"xv\3\2\2\2xw\3\2\2\2y\u0082\3\2\2\2z{\f\6\2\2{|\t\3\2\2|\u0081\5\22\n"+
		"\7}~\f\7\2\2~\177\t\2\2\2\177\u0081\5\20\t\2\u0080z\3\2\2\2\u0080}\3\2"+
		"\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\23\3\2\2\2\u0084\u0082\3\2\2\2\22\30\35\'\60\66;BLVXgoqx\u0080\u0082";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}