package griffio.grammar.mixins

import com.alecstrong.sql.psi.core.psi.SqlBinaryExpr
import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl
import com.alecstrong.sql.psi.core.psi.SqlExpr
import com.intellij.lang.ASTNode
import griffio.grammar.psi.PgSearchProximityOperator

/**
 * Used for `x ||| y` and `x &&& y` expressions in SqlBinaryExpr type resolver
 * https://docs.paradedb.com/documentation/full-text/match
 */
internal abstract class PgSearchMatchOperatorMixin(node: ASTNode) :
    SqlCompositeElementImpl(node),
    SqlBinaryExpr,
    PgSearchProximityOperator {

    override fun getExprList(): List<SqlExpr> {
        return children.filterIsInstance<SqlExpr>()
    }
}
