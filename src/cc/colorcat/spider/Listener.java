package cc.colorcat.spider;

public interface Listener<T> {

    void onSuccess(T data);

    void onFailure(Scrap<? extends T> scrap);
}
