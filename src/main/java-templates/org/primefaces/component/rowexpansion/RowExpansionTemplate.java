import org.primefaces.component.subtable.SubTable;

    public SubTable getSubTable() {
        for (UIComponent kid : getChildren()) {
            if (kid instanceof SubTable)
                return (SubTable) kid;
        }
        return null;
    }
