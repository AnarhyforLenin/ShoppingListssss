

package com.mirea.productapp.shoppinglist;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShoppingListUnmarshaller {
    private static final Pattern EMPTY_LINE = Pattern.compile("^\\s*$");
    private static final Pattern HEADER = Pattern.compile("\\[(.*)]");

    public static ShoppingList unmarshal(String filename) throws IOException, UnmarshallException {
        return unmarshal(new FileInputStream(filename));
    }

    public static ShoppingList unmarshal(InputStream inputStream) throws IOException, UnmarshallException {
        ShoppingList shoppingList;
        String name = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String firstLine = reader.readLine();

        if (firstLine != null) {
            Matcher matcher = HEADER.matcher(firstLine);
            if (matcher.matches()) {
                name = matcher.group(1).trim();
            }
        }

        if (name == null) {
            throw new UnmarshallException("Could not find the name of the list");
        }

        shoppingList = new ShoppingList(name);

        String line;
        while ((line = reader.readLine()) != null) {
            if (!EMPTY_LINE.matcher(line).matches()) {
                shoppingList.add(createListItem(line));
            }
        }

        return shoppingList;
    }

    private static ListItem createListItem(String item) {
        boolean isChecked = item.startsWith("//");
        int index;
        String quantity;
        String price;
        String name;

        if (isChecked) {
            item = item.substring(2);
        }

        index = item.lastIndexOf("#");

        if (index != -1) {
            quantity = item.substring(index + 1).trim();
            price = item.substring(index + 2).trim();
            name = item.substring(0, index).trim();
        } else {
            quantity = "";
            name = item.trim();
            price = item.trim();
        }

        return new ListItem(isChecked, name.trim(), quantity, price);
    }
}
