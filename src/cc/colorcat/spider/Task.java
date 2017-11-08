//package cc.colorcat.spider;
//
//public class Task {
//    private final Scrap<?> scrap;
//
//    public Task(Scrap<?> scrap) {
//        this.scrap = scrap;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Task task = (Task) o;
//
//        return scrap != null ? scrap.equals(task.scrap) : task.scrap == null;
//    }
//
//    @Override
//    public int hashCode() {
//        return scrap != null ? scrap.hashCode() : 0;
//    }
//}
