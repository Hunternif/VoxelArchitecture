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
			setState(25); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
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
				setState(27); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACKET) | (1L << DOT) | (1L << ID))) != 0) );
			setState(29);
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
			setState(31);
			selector(0);
			setState(32);
			match(LBRACE);
			setState(36);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(33);
				match(NEWLINE);
				}
				}
				setState(38);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(39);
			ruleBody();
			setState(40);
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
			setState(43); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(42);
				declaration();
				}
				}
				setState(45); 
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
			setState(47);
			((DeclarationContext)_localctx).property = match(ID);
			setState(48);
			match(COLON);
			setState(49);
			((DeclarationContext)_localctx).value = propValue();
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(50);
				match(SEMICOLON);
				}
			}

			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(53);
				match(NEWLINE);
				}
				}
				setState(58);
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
			setState(63);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new DimValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(59);
				dimExpression(0);
				}
				break;
			case 2:
				_localctx = new NumValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(60);
				numExpression(0);
				}
				break;
			case 3:
				_localctx = new EnumValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(61);
				match(ID);
				}
				break;
			case 4:
				_localctx = new StrValueContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(62);
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
			setState(73);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACKET:
				{
				_localctx = new DescendantSelectorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(66);
				match(LBRACKET);
				setState(67);
				selector(0);
				setState(68);
				match(RBRACKET);
				setState(69);
				selector(3);
				}
				break;
			case DOT:
				{
				_localctx = new ClassSelectorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(71);
				dotClass();
				}
				break;
			case ID:
				{
				_localctx = new TypeSelectorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(72);
				match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(85);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(83);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						_localctx = new ListSelectorContext(new SelectorContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(75);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(76);
						match(COMMA);
						setState(77);
						selector(7);
						}
						break;
					case 2:
						{
						_localctx = new ChildSelectorContext(new SelectorContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(78);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(79);
						match(GT);
						setState(80);
						selector(5);
						}
						break;
					case 3:
						{
						_localctx = new AndSelectorContext(new SelectorContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(81);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(82);
						dotClass();
						}
						break;
					}
					} 
				}
				setState(87);
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
			setState(88);
			match(DOT);
			setState(89);
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
			setState(100);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new NumParenExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(92);
				match(LPAREN);
				setState(93);
				numExpression(0);
				setState(94);
				match(RPAREN);
				}
				break;
			case MINUS:
				{
				_localctx = new NumMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(96);
				match(MINUS);
				setState(97);
				numExpression(3);
				}
				break;
			case INT:
				{
				_localctx = new IntLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(98);
				match(INT);
				}
				break;
			case FLOAT:
				{
				_localctx = new FloatLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(99);
				match(FLOAT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(110);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(108);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
					case 1:
						{
						_localctx = new NumBinaryOperationContext(new NumExpressionContext(_parentctx, _parentState));
						((NumBinaryOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_numExpression);
						setState(102);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(103);
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
						setState(104);
						((NumBinaryOperationContext)_localctx).right = numExpression(7);
						}
						break;
					case 2:
						{
						_localctx = new NumBinaryOperationContext(new NumExpressionContext(_parentctx, _parentState));
						((NumBinaryOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_numExpression);
						setState(105);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(106);
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
						setState(107);
						((NumBinaryOperationContext)_localctx).right = numExpression(6);
						}
						break;
					}
					} 
				}
				setState(112);
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
			setState(117);
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

				setState(114);
				numExpression(0);
				}
				break;
			case INT_PCT:
				{
				_localctx = new IntPctLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(115);
				match(INT_PCT);
				}
				break;
			case FLOAT_PCT:
				{
				_localctx = new FloatPctLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(116);
				match(FLOAT_PCT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(127);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(125);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new DimAddOperationContext(new DimExpressionContext(_parentctx, _parentState));
						((DimAddOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dimExpression);
						setState(119);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(120);
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
						setState(121);
						((DimAddOperationContext)_localctx).right = dimExpression(5);
						}
						break;
					case 2:
						{
						_localctx = new DimMultOperationContext(new DimExpressionContext(_parentctx, _parentState));
						((DimMultOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_dimExpression);
						setState(122);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(123);
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
						setState(124);
						((DimMultOperationContext)_localctx).right = numExpression(0);
						}
						break;
					}
					} 
				}
				setState(129);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3 \u0085\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2"+
		"\7\2\27\n\2\f\2\16\2\32\13\2\6\2\34\n\2\r\2\16\2\35\3\2\3\2\3\3\3\3\3"+
		"\3\7\3%\n\3\f\3\16\3(\13\3\3\3\3\3\3\3\3\4\6\4.\n\4\r\4\16\4/\3\5\3\5"+
		"\3\5\3\5\5\5\66\n\5\3\5\7\59\n\5\f\5\16\5<\13\5\3\6\3\6\3\6\3\6\5\6B\n"+
		"\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7L\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\7\7V\n\7\f\7\16\7Y\13\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\5\tg\n\t\3\t\3\t\3\t\3\t\3\t\3\t\7\to\n\t\f\t\16\tr\13\t\3\n"+
		"\3\n\3\n\3\n\5\nx\n\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0080\n\n\f\n\16\n\u0083"+
		"\13\n\3\n\2\5\f\20\22\13\2\4\6\b\n\f\16\20\22\2\4\3\2\r\16\3\2\13\f\2"+
		"\u0092\2\33\3\2\2\2\4!\3\2\2\2\6-\3\2\2\2\b\61\3\2\2\2\nA\3\2\2\2\fK\3"+
		"\2\2\2\16Z\3\2\2\2\20f\3\2\2\2\22w\3\2\2\2\24\30\5\4\3\2\25\27\7\3\2\2"+
		"\26\25\3\2\2\2\27\32\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\34\3\2\2\2"+
		"\32\30\3\2\2\2\33\24\3\2\2\2\34\35\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2"+
		"\36\37\3\2\2\2\37 \7\2\2\3 \3\3\2\2\2!\"\5\f\7\2\"&\7\23\2\2#%\7\3\2\2"+
		"$#\3\2\2\2%(\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\')\3\2\2\2(&\3\2\2\2)*\5\6\4"+
		"\2*+\7\24\2\2+\5\3\2\2\2,.\5\b\5\2-,\3\2\2\2./\3\2\2\2/-\3\2\2\2/\60\3"+
		"\2\2\2\60\7\3\2\2\2\61\62\7\36\2\2\62\63\7\30\2\2\63\65\5\n\6\2\64\66"+
		"\7\31\2\2\65\64\3\2\2\2\65\66\3\2\2\2\66:\3\2\2\2\679\7\3\2\28\67\3\2"+
		"\2\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;\t\3\2\2\2<:\3\2\2\2=B\5\22\n\2>B\5"+
		"\20\t\2?B\7\36\2\2@B\7\6\2\2A=\3\2\2\2A>\3\2\2\2A?\3\2\2\2A@\3\2\2\2B"+
		"\13\3\2\2\2CD\b\7\1\2DE\7\21\2\2EF\5\f\7\2FG\7\22\2\2GH\5\f\7\5HL\3\2"+
		"\2\2IL\5\16\b\2JL\7\36\2\2KC\3\2\2\2KI\3\2\2\2KJ\3\2\2\2LW\3\2\2\2MN\f"+
		"\b\2\2NO\7\33\2\2OV\5\f\7\tPQ\f\6\2\2QR\7\26\2\2RV\5\f\7\7ST\f\7\2\2T"+
		"V\5\16\b\2UM\3\2\2\2UP\3\2\2\2US\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2"+
		"X\r\3\2\2\2YW\3\2\2\2Z[\7\32\2\2[\\\7\36\2\2\\\17\3\2\2\2]^\b\t\1\2^_"+
		"\7\17\2\2_`\5\20\t\2`a\7\20\2\2ag\3\2\2\2bc\7\f\2\2cg\5\20\t\5dg\7\7\2"+
		"\2eg\7\b\2\2f]\3\2\2\2fb\3\2\2\2fd\3\2\2\2fe\3\2\2\2gp\3\2\2\2hi\f\b\2"+
		"\2ij\t\2\2\2jo\5\20\t\tkl\f\7\2\2lm\t\3\2\2mo\5\20\t\bnh\3\2\2\2nk\3\2"+
		"\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2q\21\3\2\2\2rp\3\2\2\2st\b\n\1\2tx\5"+
		"\20\t\2ux\7\t\2\2vx\7\n\2\2ws\3\2\2\2wu\3\2\2\2wv\3\2\2\2x\u0081\3\2\2"+
		"\2yz\f\6\2\2z{\t\3\2\2{\u0080\5\22\n\7|}\f\7\2\2}~\t\2\2\2~\u0080\5\20"+
		"\t\2\177y\3\2\2\2\177|\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081"+
		"\u0082\3\2\2\2\u0082\23\3\2\2\2\u0083\u0081\3\2\2\2\22\30\35&/\65:AKU"+
		"Wfnpw\177\u0081";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}