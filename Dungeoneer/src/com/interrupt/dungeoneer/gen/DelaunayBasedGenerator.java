package com.interrupt.dungeoneer.gen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.entities.Entity;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.spawn.SpawnController;
import com.interrupt.dungeoneer.tiles.Tile;

import java.util.*;

/**
 * Delaunay + MST based generator. Produces a connected set of rooms and corridors
 * from a 2D point set, then carves them into a Level grid and performs basic spawning
 * via the provided SpawnController.
 */
public final class DelaunayBasedGenerator {
    /** Generation parameters (with safe defaults). */
    public static final class GeneratorParams {
        public int gridWidth = 68;
        public int gridHeight = 68;
        public int targetRooms = 18;
        public int minRoom = 4;
        public int maxRoom = 8;
        public float loopRatio = 0.15f; // proportion of extra edges over MST size
        public int corridorWidth = 1;
        public String biome = "DUNGEON";
        public int difficultyTier = 1;
    }

    private static final class Pt {
        final float x, y;
        Pt(float x, float y) { this.x = x; this.y = y; }
    }
    private static final class Edge { final Pt a, b; Edge(Pt a, Pt b){ this.a=a; this.b=b; }}
    private static final class Tri { final Pt a,b,c; final Circ circ; Tri(Pt a, Pt b, Pt c){ this.a=a; this.b=b; this.c=c; this.circ = Circ.of(a,b,c);} }
    private static final class Circ { final float x,y,r2; private Circ(float x,float y,float r2){this.x=x;this.y=y;this.r2=r2;} static Circ of(Pt a, Pt b, Pt c){
        float ax=a.x, ay=a.y, bx=b.x, by=b.y, cx=c.x, cy=c.y;
        float A = bx-ax, B = by-ay, C = cx-ax, D = cy-ay;
        float E = A*(ax+bx) + B*(ay+by);
        float F = C*(ax+cx) + D*(ay+cy);
        float G = 2f*(A*(cy-by) - B*(cx-bx));
        if (Math.abs(G) < 1e-6f) {
            // Colinear; use large circle
            float mx = (ax+bx+cx)/3f, my = (ay+by+cy)/3f; float dx=ax-mx, dy=ay-my; float r2=dx*dx+dy*dy; return new Circ(mx,my,r2+1e6f);
        }
        float cx_ = (D*E - B*F)/G; float cy_ = (A*F - C*E)/G; float dx=ax-cx_, dy=ay-cy_; return new Circ(cx_, cy_, dx*dx+dy*dy);
    } boolean contains(Pt p){ float dx=p.x-x, dy=p.y-y; return dx*dx+dy*dy <= r2; }}

    private static final class Node { final Pt p; final int id; Node(int id, Pt p){ this.id=id; this.p=p; }}
    private static final class GEdge { final Node a,b; final float w; GEdge(Node a, Node b){ this.a=a; this.b=b; this.w=dist(a.p,b.p);} }

    public Level generate(long seed, GeneratorParams p, SpawnController spawns) {
        Random rng = new Random(seed);

        // 1) Sample points inside a padded bbox
        Array<Pt> pts = new Array<>();
        for (int i = 0; i < p.targetRooms; i++) {
            float x = 3 + rng.nextFloat() * (p.gridWidth - 6);
            float y = 3 + rng.nextFloat() * (p.gridHeight - 6);
            pts.add(new Pt(x, y));
        }

        // 2) Delaunay triangulation via Bowyer–Watson
        Array<Tri> tris = triangulate(pts);

        // 3) Build graph from triangle edges
        Array<Node> nodes = new Array<>();
        for (int i = 0; i < pts.size; i++) nodes.add(new Node(i, pts.get(i)));
        Array<GEdge> edges = collectEdges(tris, nodes);

        // 4) MST (Kruskal) + extra loops
        Array<GEdge> mst = kruskal(nodes, edges);
        int extras = Math.max(0, Math.round(mst.size * p.loopRatio));
        Array<GEdge> graph = new Array<>(); graph.addAll(mst);
        Collections.shuffle(edges.toList(), rng);
        for (GEdge e : edges) { if (extras <= 0) break; if (!graphContains(graph, e)) { graph.add(e); extras--; } }

        // 5) Create room rects around nodes
        Array<Room> rooms = new Array<>();
        for (Node n : nodes) {
            int rw = MathUtils.random(p.minRoom, p.maxRoom);
            int rh = MathUtils.random(p.minRoom, p.maxRoom);
            int cx = Math.round(n.p.x); int cy = Math.round(n.p.y);
            rooms.add(new Room(cx - rw/2, cy - rh/2, rw, rh));
        }

        // 6) Rasterize
        Level level = new Level(p.gridWidth, p.gridHeight);
        // init all tiles as solid walls
        for (int y = 0; y < p.gridHeight; y++) {
            for (int x = 0; x < p.gridWidth; x++) {
                level.setTile(x, y, Tile.NewSolidTile());
            }
        }
        // carve rooms
        for (Room r : rooms) carveRoom(level, r);
        // carve corridors along graph edges
        for (GEdge e : graph) carveCorridor(level, Math.round(e.a.p.x), Math.round(e.a.p.y), Math.round(e.b.p.x), Math.round(e.b.p.y), p.corridorWidth);

        // pick player start
        if (rooms.size > 0) {
            Room start = rooms.first();
            level.playerStartX = clamp(start.cx(), 1, p.gridWidth-2);
            level.playerStartY = clamp(start.cy(), 1, p.gridHeight-2);
        }

        // 7) Ensure a way down exists (place a down stairs in the farthest room from start)
        if (rooms.size > 1) {
            Room start = rooms.first();
            Room far = start;
            float best = -1f;
            for (Room r : rooms) {
                float d = (r.cx()-start.cx())*(r.cx()-start.cx()) + (r.cy()-start.cy())*(r.cy()-start.cy());
                if (d > best) { best = d; far = r; }
            }
            com.interrupt.dungeoneer.entities.Stairs down = new com.interrupt.dungeoneer.entities.Stairs(com.interrupt.dungeoneer.entities.Stairs.StairDirection.down);
            down.x = far.cx() + 0.5f; down.y = far.cy() + 0.5f;
            down.z = level.getTile(far.cx(), far.cy()).getFloorHeight(far.cx() + 0.5f, far.cy() + 0.5f) + 0.5f;
            level.SpawnEntity(down);
        }

        // 8) Basic decoration & spawning via SpawnController
        for (Room r : rooms) {
            SpawnController.RoomCtx ctx = new SpawnController.RoomCtx(p.biome, null, p.difficultyTier);
            // enemies
            spawns.pickEnemy(ctx, new Random(seed ^ r.hashCode())).applyTo(level, r.cx(), r.cy());
            // items
            spawns.pickItems(ctx, new Random(seed + r.hashCode())).applyTo(level, r.cx(), r.cy());
        }

        return level;
    }

    // --- helpers ---
    private static float dist(Pt a, Pt b) { float dx=a.x-b.x, dy=a.y-b.y; return (float)Math.sqrt(dx*dx+dy*dy); }
    private static int clamp(int v, int lo, int hi){ return Math.max(lo, Math.min(hi, v)); }

    private static Array<Tri> triangulate(Array<Pt> pts) {
        // super triangle
        float minX=Float.MAX_VALUE, minY=Float.MAX_VALUE, maxX=-Float.MAX_VALUE, maxY=-Float.MAX_VALUE;
        for (Pt p : pts) { if (p.x < minX) minX = p.x; if (p.y < minY) minY = p.y; if (p.x > maxX) maxX = p.x; if (p.y > maxY) maxY = p.y; }
        float dx = maxX - minX, dy = maxY - minY; float dmax = Math.max(dx, dy);
        Pt p1 = new Pt(minX - 2*dmax, minY - dmax);
        Pt p2 = new Pt(minX + 0.5f*dx, maxY + 2*dmax);
        Pt p3 = new Pt(maxX + 2*dmax, minY - dmax);
        Array<Tri> tris = new Array<>(); tris.add(new Tri(p1,p2,p3));

        for (Pt p : pts) {
            Array<Tri> bad = new Array<>();
            for (Tri t : tris) if (t.circ.contains(p)) bad.add(t);
            Array<Edge> poly = polygonize(bad);
            tris.removeAll(bad, true);
            for (Edge e : poly) tris.add(new Tri(e.a, e.b, p));
        }
        // remove triangles touching super triangle
        Array<Tri> kept = new Array<>();
        for (Tri t : tris) {
            if (t.a==p1||t.a==p2||t.a==p3||t.b==p1||t.b==p2||t.b==p3||t.c==p1||t.c==p2||t.c==p3) continue;
            kept.add(t);
        }
        return kept;
    }

    private static Array<Edge> polygonize(Array<Tri> bad) {
        Array<Edge> edges = new Array<>();
        for (Tri t : bad) {
            edges.add(new Edge(t.a, t.b));
            edges.add(new Edge(t.b, t.c));
            edges.add(new Edge(t.c, t.a));
        }
        // remove shared edges
        Array<Edge> boundary = new Array<>();
        for (int i = 0; i < edges.size; i++) {
            Edge e = edges.get(i);
            boolean shared = false;
            for (int j = 0; j < edges.size; j++) {
                if (i==j) continue;
                if (eq(e.a, edges.get(j).b) && eq(e.b, edges.get(j).a)) { shared = true; break; }
                if (eq(e.a, edges.get(j).a) && eq(e.b, edges.get(j).b)) { shared = true; break; }
            }
            if (!shared) boundary.add(e);
        }
        return boundary;
    }
    private static boolean eq(Pt a, Pt b){ return a==b || (Math.abs(a.x-b.x)<1e-5f && Math.abs(a.y-b.y)<1e-5f); }

    private static Array<GEdge> collectEdges(Array<Tri> tris, Array<Node> nodes) {
        Array<GEdge> edges = new Array<>();
        for (Tri t : tris) {
            addEdge(edges, nodes, t.a, t.b);
            addEdge(edges, nodes, t.b, t.c);
            addEdge(edges, nodes, t.c, t.a);
        }
        return edges;
    }
    private static void addEdge(Array<GEdge> edges, Array<Node> nodes, Pt a, Pt b) {
        Node na = findNode(nodes, a); Node nb = findNode(nodes, b);
        if (na == null || nb == null || na == nb) return;
        // avoid duplicates
        for (GEdge e : edges) if ((e.a==na && e.b==nb) || (e.a==nb && e.b==na)) return;
        edges.add(new GEdge(na, nb));
    }
    private static Node findNode(Array<Node> nodes, Pt p){ for (Node n : nodes) if (n.p==p) return n; return null; }

    private static Array<GEdge> kruskal(Array<Node> nodes, Array<GEdge> edges) {
        Array<GEdge> result = new Array<>();
        Array<GEdge> sorted = new Array<>(); sorted.addAll(edges);
        sorted.sort(Comparator.comparingDouble(e -> e.w));

        // disjoint set
        Map<Node,Node> parent = new HashMap<>();
        for (Node n : nodes) parent.put(n, n);

        for (GEdge e : sorted) {
            Node ra = find(parent, e.a), rb = find(parent, e.b);
            if (ra != rb) {
                result.add(e);
                parent.put(ra, rb);
            }
        }
        return result;
    }
    private static Node find(Map<Node,Node> parent, Node n){ Node p=parent.get(n); if(p==n) return n; Node r=find(parent,p); parent.put(n,r); return r; }
    private static boolean graphContains(Array<GEdge> g, GEdge e){ for(GEdge ge:g) if ((ge.a==e.a&&ge.b==e.b)||(ge.a==e.b&&ge.b==e.a)) return true; return false; }

    private static final class Room {
        final int x,y,w,h;
        Room(int x,int y,int w,int h){ this.x=x; this.y=y; this.w=w; this.h=h; }
        int cx(){ return x + w/2; }
        int cy(){ return y + h/2; }
    }

    private static void carveRoom(Level l, Room r) {
        for (int yy = r.y; yy < r.y + r.h; yy++) {
            for (int xx = r.x; xx < r.x + r.w; xx++) {
                if (l.inBounds(xx, yy)) l.setTile(xx, yy, Tile.EmptyTile());
            }
        }
    }

    private static void carveCorridor(Level l, int x0, int y0, int x1, int y1, int w) {
        // Bresenham, with simple width
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        int x = x0, y = y0;
        while (true) {
            carveDisc(l, x, y, w);
            if (x == x1 && y == y1) break;
            int e2 = 2 * err;
            if (e2 >= dy) { err += dy; x += sx; }
            if (e2 <= dx) { err += dx; y += sy; }
        }
    }
    private static void carveDisc(Level l, int cx, int cy, int r) {
        for (int y=cy-r; y<=cy+r; y++) {
            for (int x=cx-r; x<=cx+r; x++) {
                if (!l.inBounds(x,y)) continue;
                int dx=x-cx, dy=y-cy; if (dx*dx+dy*dy<=r*r) l.setTile(x,y, Tile.EmptyTile());
            }
        }
    }
}
