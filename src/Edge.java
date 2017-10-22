import java.util.Objects;

class Edge {
    private int from, to, value;
    private int color;
    private boolean vis;

    // Fix by PMD -- ruleset 'design'
    // constructor 'Edge()' deleted

    // Fix by PMD -- ruleset 'design'
    Edge(final int fromIn,final int toIn,final int valueIn) {
        from = fromIn;
        to = toIn;
        value = valueIn;
        vis = false;
    }

    public int getValue() {
        return value;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void setValue(int val) {
        value = val;
    }

    public void setVis(boolean b) {
        vis = b;
    }

    public boolean getVis() {
        return vis;
    }

    public String getColor() {
        String retColor = null;
        if (color == 1) {
            retColor = "red";
        }
        if (color == 2){
            retColor = "green";
        }
        return retColor;
    }

    public void setColor(String clr) {
        if (Objects.equals(clr, "red")) {
            color = 1;
        } else if (Objects.equals(clr, "green"))
        {
            color = 2;
        } else
        {
            color = 0;
        }
    }

}
