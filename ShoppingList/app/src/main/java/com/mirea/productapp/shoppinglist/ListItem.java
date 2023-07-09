

package com.mirea.productapp.shoppinglist;

public class ListItem {
    private boolean isChecked;
    private String description;
    private String quantity;
    private String price;

    public ListItem(boolean isChecked, String description, String quantity, String price) {
        this.isChecked = isChecked;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() { return price; }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    static class ListItemWithID extends ListItem {
        private final int id;

        public ListItemWithID(int id, ListItem item) {
            super(item.isChecked, item.description, item.quantity, item.price);
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }
}
