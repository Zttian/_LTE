package com.tky.lte.db;

import com.tky.lte.LTEApp;
import com.tky.lte.gen.AddressBookDao;
import com.tky.lte.ui.entity.AddressBook;

import java.util.List;

/**
 * 增加单个数据
 * getShopDao().insert(shop);
 * getShopDao().insertOrReplace(shop);
 * 增加多个数据
 * getShopDao().insertInTx(shopList);
 * getShopDao().insertOrReplaceInTx(shopList);
 * 查询全部
 * List< Shop> list = getShopDao().loadAll();
 * List< Shop> list = getShopDao().queryBuilder().list();
 * 查询附加单个条件
 * .where()
 * .whereOr()
 * 查询附加多个条件
 * .where(, , ,)
 * .whereOr(, , ,)
 * 查询附加排序
 * .orderDesc()
 * .orderAsc()
 * 查询限制当页个数
 * .limit()
 * 查询总个数
 * .count()
 * 修改单个数据
 * getShopDao().update(shop);
 * 修改多个数据
 * getShopDao().updateInTx(shopList);
 * 删除单个数据
 * getTABUserDao().delete(user);
 * 删除多个数据
 * getUserDao().deleteInTx(userList);
 * 删除数据ByKey
 * getTABUserDao().deleteByKey();
 */

public class ABookDao {
    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param book
     */
    public static void insertAddressBook(AddressBook book) {
        LTEApp.getDaoInstance().getAddressBookDao().insertOrReplace(book);
    }

    /**
     * 删除数据
     *
     * @param book
     */
    public static void deleteAddressBook(AddressBook book) {
//        LTEApp.getDaoInstance().getAddressBookDao().deleteByKey(id);
        LTEApp.getDaoInstance().getAddressBookDao().delete(book);
    }

    /**
     * 更新数据
     */
    public static void updateAddressBook(AddressBook book) {
        LTEApp.getDaoInstance().getAddressBookDao().update(book);
    }

    /**
     * 查询指定条件下的所有数据
     *
     * @return
     */
    public static List<AddressBook> queryAddressBook(String number) {
        return LTEApp.getDaoInstance().getAddressBookDao().queryBuilder().where(AddressBookDao.Properties.Number.eq(number)).list();
    }

    /**
     * 查询所有数据
     * @return
     */
    public static List<AddressBook> queryAll() {
        return LTEApp.getDaoInstance().getAddressBookDao().loadAll();
    }
}
