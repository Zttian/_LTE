package com.tky.lte.db;

import com.tky.lte.LTEApp;
import com.tky.lte.gen.CallJilEntityDao;
import com.tky.lte.ui.entity.CallJilEntity;
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
public class CallJiDao {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param call
     */
    public static void insertCallJilEntity(CallJilEntity call) {
        LTEApp.getDaoInstance().getCallJilEntityDao().insertOrReplace(call);
    }

    /**
     * 删除数据
     *
     * @param call
     */
    public static void deleteCallJilEntity(CallJilEntity call) {
        LTEApp.getDaoInstance().getCallJilEntityDao().delete(call);
    }

    /**
     * 删除数据
     *
     * @param
     */
    public static void deleteCallJilList(List<CallJilEntity> list) {
        LTEApp.getDaoInstance().getCallJilEntityDao().deleteInTx(list);
    }

    /**
     * 更新数据
     */
    public static void updateCallJilEntity(CallJilEntity call) {
        LTEApp.getDaoInstance().getCallJilEntityDao().update(call);
    }

    /**
     * 查询指定条件下的所有数据
     *
     * @return
     */
    public static List<CallJilEntity> queryCallJilEntity(String PeerNumber) {
        return LTEApp.getDaoInstance().getCallJilEntityDao().queryBuilder().where(CallJilEntityDao.Properties.PeerNumber.eq(PeerNumber)).list();
    }

    /**
     * 查询指定条件下的所有数据
     *
     * @return
     */
    public static List<CallJilEntity> queryCallJilList(String PeerNumber) {
        return LTEApp.getDaoInstance().getCallJilEntityDao().queryBuilder().where(CallJilEntityDao.Properties.PeerNumber.eq(PeerNumber)).list();
    }


    /**
     * 查询所有数据
     * @return
     */
    public static List<CallJilEntity> queryAll() {
        return LTEApp.getDaoInstance().getCallJilEntityDao().loadAll();
    }
    
}
