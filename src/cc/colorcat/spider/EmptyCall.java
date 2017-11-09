package cc.colorcat.spider;

final class EmptyCall implements Call {
    private final Scrap scrap;

    EmptyCall(Scrap scrap) {
        this.scrap = scrap;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Scrap seed() {
        return scrap;
    }

    @Override
    public void execute() {

    }

    @Override
    public void run() {

    }
}
