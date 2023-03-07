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
		DOT=24, COMMA=25, SINGLEQUOTE=26, DOUBLEQUOTE=27, ID=28, LINE_COMMENT=29, 
		BLOCK_COMMENT=30;
	public static final int
		RULE_stylesheet = 0, RULE_styleRule = 1, RULE_ruleBody = 2, RULE_declaration = 3, 
		RULE_propValue = 4, RULE_selector = 5, RULE_numExpression = 6, RULE_comment = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"stylesheet", "styleRule", "ruleBody", "declaration", "propValue", "selector", 
			"numExpression", "comment"
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
			"COMMA", "SINGLEQUOTE", "DOUBLEQUOTE", "ID", "LINE_COMMENT", "BLOCK_COMMENT"
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
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
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
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NEWLINE) | (1L << LBRACKET) | (1L << DOT) | (1L << ID) | (1L << LINE_COMMENT) | (1L << BLOCK_COMMENT))) != 0)) {
				{
				setState(19);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LBRACKET:
				case DOT:
				case ID:
					{
					setState(16);
					styleRule();
					}
					break;
				case LINE_COMMENT:
				case BLOCK_COMMENT:
					{
					setState(17);
					comment();
					}
					break;
				case NEWLINE:
					{
					setState(18);
					match(NEWLINE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(24);
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
			setState(26);
			selector(0);
			setState(27);
			match(LBRACE);
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(28);
				match(NEWLINE);
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(34);
			ruleBody();
			setState(35);
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
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
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
			setState(39); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(39);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ID:
					{
					setState(37);
					declaration();
					}
					break;
				case LINE_COMMENT:
				case BLOCK_COMMENT:
					{
					setState(38);
					comment();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(41); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ID) | (1L << LINE_COMMENT) | (1L << BLOCK_COMMENT))) != 0) );
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
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
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
			setState(43);
			((DeclarationContext)_localctx).property = match(ID);
			setState(44);
			match(COLON);
			setState(45);
			((DeclarationContext)_localctx).value = propValue();
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(46);
				match(SEMICOLON);
				}
			}

			setState(50);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(49);
				comment();
				}
				break;
			}
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(52);
				match(NEWLINE);
				}
				}
				setState(57);
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
	public static class InheritValueContext extends PropValueContext {
		public TerminalNode INHERIT() { return getToken(StyleGrammarParser.INHERIT, 0); }
		public InheritValueContext(PropValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterInheritValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitInheritValue(this);
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
			setState(62);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case INT_PCT:
			case FLOAT_PCT:
			case MINUS:
			case LPAREN:
				_localctx = new NumValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(58);
				numExpression(0);
				}
				break;
			case INHERIT:
				_localctx = new InheritValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(59);
				match(INHERIT);
				}
				break;
			case ID:
				_localctx = new EnumValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(60);
				match(ID);
				}
				break;
			case STR:
				_localctx = new StrValueContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(61);
				match(STR);
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		public SelectorContext ancestor;
		public SelectorContext descendant;
		public TerminalNode LBRACKET() { return getToken(StyleGrammarParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(StyleGrammarParser.RBRACKET, 0); }
		public List<SelectorContext> selector() {
			return getRuleContexts(SelectorContext.class);
		}
		public SelectorContext selector(int i) {
			return getRuleContext(SelectorContext.class,i);
		}
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
		public SelectorContext parent;
		public SelectorContext child;
		public TerminalNode GT() { return getToken(StyleGrammarParser.GT, 0); }
		public List<SelectorContext> selector() {
			return getRuleContexts(SelectorContext.class);
		}
		public SelectorContext selector(int i) {
			return getRuleContext(SelectorContext.class,i);
		}
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
	public static class OrSelectorContext extends SelectorContext {
		public SelectorContext left;
		public SelectorContext right;
		public TerminalNode COMMA() { return getToken(StyleGrammarParser.COMMA, 0); }
		public List<SelectorContext> selector() {
			return getRuleContexts(SelectorContext.class);
		}
		public SelectorContext selector(int i) {
			return getRuleContext(SelectorContext.class,i);
		}
		public OrSelectorContext(SelectorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterOrSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitOrSelector(this);
		}
	}
	public static class AndSelectorContext extends SelectorContext {
		public SelectorContext left;
		public SelectorContext right;
		public List<SelectorContext> selector() {
			return getRuleContexts(SelectorContext.class);
		}
		public SelectorContext selector(int i) {
			return getRuleContext(SelectorContext.class,i);
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
		public Token classname;
		public TerminalNode DOT() { return getToken(StyleGrammarParser.DOT, 0); }
		public TerminalNode ID() { return getToken(StyleGrammarParser.ID, 0); }
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

				setState(65);
				match(LBRACKET);
				setState(66);
				((DescendantSelectorContext)_localctx).ancestor = selector(0);
				setState(67);
				match(RBRACKET);
				setState(68);
				((DescendantSelectorContext)_localctx).descendant = selector(4);
				}
				break;
			case DOT:
				{
				_localctx = new ClassSelectorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(70);
				match(DOT);
				setState(71);
				((ClassSelectorContext)_localctx).classname = match(ID);
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
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(83);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
					case 1:
						{
						_localctx = new AndSelectorContext(new SelectorContext(_parentctx, _parentState));
						((AndSelectorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(75);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(76);
						((AndSelectorContext)_localctx).right = selector(7);
						}
						break;
					case 2:
						{
						_localctx = new ChildSelectorContext(new SelectorContext(_parentctx, _parentState));
						((ChildSelectorContext)_localctx).parent = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(77);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(78);
						match(GT);
						setState(79);
						((ChildSelectorContext)_localctx).child = selector(6);
						}
						break;
					case 3:
						{
						_localctx = new OrSelectorContext(new SelectorContext(_parentctx, _parentState));
						((OrSelectorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_selector);
						setState(80);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(81);
						match(COMMA);
						setState(82);
						((OrSelectorContext)_localctx).right = selector(2);
						}
						break;
					}
					} 
				}
				setState(87);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
	public static class IntPctLiteralContext extends NumExpressionContext {
		public TerminalNode INT_PCT() { return getToken(StyleGrammarParser.INT_PCT, 0); }
		public IntPctLiteralContext(NumExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterIntPctLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitIntPctLiteral(this);
		}
	}
	public static class FloatPctLiteralContext extends NumExpressionContext {
		public TerminalNode FLOAT_PCT() { return getToken(StyleGrammarParser.FLOAT_PCT, 0); }
		public FloatPctLiteralContext(NumExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterFloatPctLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitFloatPctLiteral(this);
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
		public TerminalNode TILDE() { return getToken(StyleGrammarParser.TILDE, 0); }
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
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_numExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new NumParenExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(89);
				match(LPAREN);
				setState(90);
				numExpression(0);
				setState(91);
				match(RPAREN);
				}
				break;
			case MINUS:
				{
				_localctx = new NumMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(93);
				match(MINUS);
				setState(94);
				numExpression(5);
				}
				break;
			case INT:
				{
				_localctx = new IntLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(95);
				match(INT);
				}
				break;
			case INT_PCT:
				{
				_localctx = new IntPctLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(96);
				match(INT_PCT);
				}
				break;
			case FLOAT:
				{
				_localctx = new FloatLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97);
				match(FLOAT);
				}
				break;
			case FLOAT_PCT:
				{
				_localctx = new FloatPctLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(98);
				match(FLOAT_PCT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(112);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(110);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
					case 1:
						{
						_localctx = new NumBinaryOperationContext(new NumExpressionContext(_parentctx, _parentState));
						((NumBinaryOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_numExpression);
						setState(101);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(102);
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
						setState(103);
						((NumBinaryOperationContext)_localctx).right = numExpression(10);
						}
						break;
					case 2:
						{
						_localctx = new NumBinaryOperationContext(new NumExpressionContext(_parentctx, _parentState));
						((NumBinaryOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_numExpression);
						setState(104);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(105);
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
						setState(106);
						((NumBinaryOperationContext)_localctx).right = numExpression(9);
						}
						break;
					case 3:
						{
						_localctx = new NumBinaryOperationContext(new NumExpressionContext(_parentctx, _parentState));
						((NumBinaryOperationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_numExpression);
						setState(107);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(108);
						((NumBinaryOperationContext)_localctx).op = match(TILDE);
						setState(109);
						((NumBinaryOperationContext)_localctx).right = numExpression(8);
						}
						break;
					}
					} 
				}
				setState(114);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
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

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode LINE_COMMENT() { return getToken(StyleGrammarParser.LINE_COMMENT, 0); }
		public TerminalNode BLOCK_COMMENT() { return getToken(StyleGrammarParser.BLOCK_COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StyleGrammarListener ) ((StyleGrammarListener)listener).exitComment(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_comment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			_la = _input.LA(1);
			if ( !(_la==LINE_COMMENT || _la==BLOCK_COMMENT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 5:
			return selector_sempred((SelectorContext)_localctx, predIndex);
		case 6:
			return numExpression_sempred((NumExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean selector_sempred(SelectorContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean numExpression_sempred(NumExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 9);
		case 4:
			return precpred(_ctx, 8);
		case 5:
			return precpred(_ctx, 7);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3 x\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\7\2\26\n"+
		"\2\f\2\16\2\31\13\2\3\2\3\2\3\3\3\3\3\3\7\3 \n\3\f\3\16\3#\13\3\3\3\3"+
		"\3\3\3\3\4\3\4\6\4*\n\4\r\4\16\4+\3\5\3\5\3\5\3\5\5\5\62\n\5\3\5\5\5\65"+
		"\n\5\3\5\7\58\n\5\f\5\16\5;\13\5\3\6\3\6\3\6\3\6\5\6A\n\6\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\5\7L\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7"+
		"V\n\7\f\7\16\7Y\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bf"+
		"\n\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\bq\n\b\f\b\16\bt\13\b\3\t\3"+
		"\t\3\t\2\4\f\16\n\2\4\6\b\n\f\16\20\2\5\3\2\r\16\3\2\13\f\3\2\37 \2\u0088"+
		"\2\27\3\2\2\2\4\34\3\2\2\2\6)\3\2\2\2\b-\3\2\2\2\n@\3\2\2\2\fK\3\2\2\2"+
		"\16e\3\2\2\2\20u\3\2\2\2\22\26\5\4\3\2\23\26\5\20\t\2\24\26\7\3\2\2\25"+
		"\22\3\2\2\2\25\23\3\2\2\2\25\24\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27"+
		"\30\3\2\2\2\30\32\3\2\2\2\31\27\3\2\2\2\32\33\7\2\2\3\33\3\3\2\2\2\34"+
		"\35\5\f\7\2\35!\7\23\2\2\36 \7\3\2\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2"+
		"\2!\"\3\2\2\2\"$\3\2\2\2#!\3\2\2\2$%\5\6\4\2%&\7\24\2\2&\5\3\2\2\2\'*"+
		"\5\b\5\2(*\5\20\t\2)\'\3\2\2\2)(\3\2\2\2*+\3\2\2\2+)\3\2\2\2+,\3\2\2\2"+
		",\7\3\2\2\2-.\7\36\2\2./\7\30\2\2/\61\5\n\6\2\60\62\7\31\2\2\61\60\3\2"+
		"\2\2\61\62\3\2\2\2\62\64\3\2\2\2\63\65\5\20\t\2\64\63\3\2\2\2\64\65\3"+
		"\2\2\2\659\3\2\2\2\668\7\3\2\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3"+
		"\2\2\2:\t\3\2\2\2;9\3\2\2\2<A\5\16\b\2=A\7\5\2\2>A\7\36\2\2?A\7\6\2\2"+
		"@<\3\2\2\2@=\3\2\2\2@>\3\2\2\2@?\3\2\2\2A\13\3\2\2\2BC\b\7\1\2CD\7\21"+
		"\2\2DE\5\f\7\2EF\7\22\2\2FG\5\f\7\6GL\3\2\2\2HI\7\32\2\2IL\7\36\2\2JL"+
		"\7\36\2\2KB\3\2\2\2KH\3\2\2\2KJ\3\2\2\2LW\3\2\2\2MN\f\b\2\2NV\5\f\7\t"+
		"OP\f\7\2\2PQ\7\26\2\2QV\5\f\7\bRS\f\3\2\2ST\7\33\2\2TV\5\f\7\4UM\3\2\2"+
		"\2UO\3\2\2\2UR\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2X\r\3\2\2\2YW\3\2"+
		"\2\2Z[\b\b\1\2[\\\7\17\2\2\\]\5\16\b\2]^\7\20\2\2^f\3\2\2\2_`\7\f\2\2"+
		"`f\5\16\b\7af\7\7\2\2bf\7\t\2\2cf\7\b\2\2df\7\n\2\2eZ\3\2\2\2e_\3\2\2"+
		"\2ea\3\2\2\2eb\3\2\2\2ec\3\2\2\2ed\3\2\2\2fr\3\2\2\2gh\f\13\2\2hi\t\2"+
		"\2\2iq\5\16\b\fjk\f\n\2\2kl\t\3\2\2lq\5\16\b\13mn\f\t\2\2no\7\27\2\2o"+
		"q\5\16\b\npg\3\2\2\2pj\3\2\2\2pm\3\2\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2"+
		"s\17\3\2\2\2tr\3\2\2\2uv\t\4\2\2v\21\3\2\2\2\21\25\27!)+\61\649@KUWep"+
		"r";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}