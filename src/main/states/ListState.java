package main.states;

import java.util.List;
import main.utils.Input;
import main.utils.PressKey;

public abstract class ListState<T> extends State {
    private int currPage = 0;
    private final int pageSize;
    private List<T> items;

    protected ListState(int pageSize) {
        this.pageSize = pageSize;
    }

    protected ListState() {
        this(10);
    }

    protected abstract String getTitle();

    protected abstract List<T> fetchItems();

    protected abstract String getRowLabel(T item, int index);

    protected abstract void onSelect(T item);

    protected void renderExtras() {}

    protected boolean handleExtra(String input) { return false; }

    @Override
    public void render() {
        items = fetchItems();
        int total = items.size();
        int pages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        if (currPage >= pages) currPage = pages - 1;

        System.out.println("--- " + getTitle() + " ---");

        if (items.isEmpty()) {
            System.out.println("No results found.");
        } else {
            int start = currPage * pageSize;
            int end = Math.min(start + pageSize, total);

            for (int i = start; i < end; i++) {
                System.out.println("[" + (i + 1) + "] " + getRowLabel(items.get(i), i));
            }
            System.out.println("\nPage " + (currPage + 1) + "/" + pages + " (" + total + " total)");
        }

        renderExtras();
        System.out.println("\nH. Prev  J. Next  0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine().trim().toUpperCase();

        if (handleExtra(input)) return;

        int total = items == null ? 0 : items.size();
        int pages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        switch (input) {
            case "H": if (currPage > 0) currPage--; break;
            case "J": if (currPage < pages - 1) currPage++; break;
            case "0": back(); break;
            default:
                try {
                    int index = Integer.parseInt(input) - 1;
                    int start = currPage * pageSize;
                    int end = Math.min(start + pageSize, total);

                    if (index >= start && index < end) {
                        onSelect(items.get(index));
                    } else {
                        System.out.println("Invalid selection.");
                        PressKey.enter();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid option.");
                    PressKey.enter();
                }
        }
    }
}