package main.states.OLD;
// package main.states;

// import java.util.List;
// import main.DatabaseManager;
// import main.models.Repair;
// import main.utils.Input;

// public class ListRepairsState extends ListState<Repair> {
//     private String search      = "";
//     private String filterState = "";
//     private boolean asc        = false;

//     @Override
//     protected String getTitle() {
//         return filterState.isEmpty() ? "ALL REPAIRS" : "REPAIRS - " + filterState;
//     }

//     @Override
//     protected List<Repair> fetchItems() {
//         return DatabaseManager.getInstance().getRepairs(search, filterState, asc, user);
//     }

//     @Override
//     protected String getRowLabel(Repair r, int index) {
//         return String.format("%-20s %-15s %s", r.getRepairCode(), r.getState(), r.getStartDate().toLocalDate());
//     }

//     @Override
//     protected void onSelect(Repair r) {
//         next(new RepairDetailState(r));
//     }

//     @Override
//     protected void renderExtras() {
//         System.out.println("F. Search  S. Filter by state  O. Toggle order (" + (asc ? "ASC" : "DESC") + ")");
//     }

//     @Override
//     protected boolean handleExtra(String input) {
//         switch (input) {
//             case "F":
//                 if(!user.getType().equals("ADMIN")) System.out.print("Search (code or client): ");
//                 else System.out.print("Search for code: ");
//                 search = Input.getScanner().nextLine().trim();
//                 return true;
//             case "S":
//                 System.out.print("State (PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, ARCHIVED): ");
//                 filterState = Input.getScanner().nextLine().trim().toUpperCase();
//                 return true;
//             case "O":
//                 asc = !asc;
//                 return true;
//             default:
//                 return false;
//         }
//     }
// }