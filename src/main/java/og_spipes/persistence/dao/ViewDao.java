package og_spipes.persistence.dao;

import og_spipes.model.view.Edge;
import og_spipes.model.view.Node;
import og_spipes.model.view.View;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Set;

@Repository
public class ViewDao extends BaseDao<View> {

    public ViewDao() {
        super(View.class);
    }

    public Set<Node> getViewNodes(URI uri) {
        return find(uri).getNodes();
    }

    public Set<Edge> getViewEdges(URI uri) {
        return find(uri).getEdges();
    }

}