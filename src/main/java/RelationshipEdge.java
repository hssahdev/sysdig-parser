import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;

class RelationshipEdge
        extends
        DefaultEdge
{
    public void setLabel(Pair<Double, Double> label) {
        this.label = label;
    }

    public Pair<Double,Double> label;

    public RelationshipEdge(Pair<Double, Double> label) {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label.toString();
    }
}