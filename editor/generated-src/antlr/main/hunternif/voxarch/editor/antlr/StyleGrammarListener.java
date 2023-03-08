// Generated from StyleGrammar.g4 by ANTLR 4.9.1
package hunternif.voxarch.editor.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link StyleGrammarParser}.
 */
public interface StyleGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link StyleGrammarParser#stylesheet}.
	 * @param ctx the parse tree
	 */
	void enterStylesheet(StyleGrammarParser.StylesheetContext ctx);
	/**
	 * Exit a parse tree produced by {@link StyleGrammarParser#stylesheet}.
	 * @param ctx the parse tree
	 */
	void exitStylesheet(StyleGrammarParser.StylesheetContext ctx);
	/**
	 * Enter a parse tree produced by {@link StyleGrammarParser#styleRule}.
	 * @param ctx the parse tree
	 */
	void enterStyleRule(StyleGrammarParser.StyleRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link StyleGrammarParser#styleRule}.
	 * @param ctx the parse tree
	 */
	void exitStyleRule(StyleGrammarParser.StyleRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link StyleGrammarParser#ruleBody}.
	 * @param ctx the parse tree
	 */
	void enterRuleBody(StyleGrammarParser.RuleBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link StyleGrammarParser#ruleBody}.
	 * @param ctx the parse tree
	 */
	void exitRuleBody(StyleGrammarParser.RuleBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link StyleGrammarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(StyleGrammarParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link StyleGrammarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(StyleGrammarParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void enterNumValue(StyleGrammarParser.NumValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void exitNumValue(StyleGrammarParser.NumValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inheritValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void enterInheritValue(StyleGrammarParser.InheritValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inheritValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void exitInheritValue(StyleGrammarParser.InheritValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code enumValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void enterEnumValue(StyleGrammarParser.EnumValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code enumValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void exitEnumValue(StyleGrammarParser.EnumValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code strValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void enterStrValue(StyleGrammarParser.StrValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code strValue}
	 * labeled alternative in {@link StyleGrammarParser#propValue}.
	 * @param ctx the parse tree
	 */
	void exitStrValue(StyleGrammarParser.StrValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code descendantSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterDescendantSelector(StyleGrammarParser.DescendantSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code descendantSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitDescendantSelector(StyleGrammarParser.DescendantSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterTypeSelector(StyleGrammarParser.TypeSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitTypeSelector(StyleGrammarParser.TypeSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code childSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterChildSelector(StyleGrammarParser.ChildSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code childSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitChildSelector(StyleGrammarParser.ChildSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterOrSelector(StyleGrammarParser.OrSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitOrSelector(StyleGrammarParser.OrSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code anySelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterAnySelector(StyleGrammarParser.AnySelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code anySelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitAnySelector(StyleGrammarParser.AnySelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterAndSelector(StyleGrammarParser.AndSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitAndSelector(StyleGrammarParser.AndSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code classSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterClassSelector(StyleGrammarParser.ClassSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code classSelector}
	 * labeled alternative in {@link StyleGrammarParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitClassSelector(StyleGrammarParser.ClassSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intPctLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void enterIntPctLiteral(StyleGrammarParser.IntPctLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intPctLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void exitIntPctLiteral(StyleGrammarParser.IntPctLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code floatPctLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void enterFloatPctLiteral(StyleGrammarParser.FloatPctLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code floatPctLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void exitFloatPctLiteral(StyleGrammarParser.FloatPctLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numParenExpression}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void enterNumParenExpression(StyleGrammarParser.NumParenExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numParenExpression}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void exitNumParenExpression(StyleGrammarParser.NumParenExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numMinusExpression}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void enterNumMinusExpression(StyleGrammarParser.NumMinusExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numMinusExpression}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void exitNumMinusExpression(StyleGrammarParser.NumMinusExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(StyleGrammarParser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(StyleGrammarParser.IntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(StyleGrammarParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(StyleGrammarParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numBinaryOperation}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void enterNumBinaryOperation(StyleGrammarParser.NumBinaryOperationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numBinaryOperation}
	 * labeled alternative in {@link StyleGrammarParser#numExpression}.
	 * @param ctx the parse tree
	 */
	void exitNumBinaryOperation(StyleGrammarParser.NumBinaryOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link StyleGrammarParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(StyleGrammarParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link StyleGrammarParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(StyleGrammarParser.CommentContext ctx);
}