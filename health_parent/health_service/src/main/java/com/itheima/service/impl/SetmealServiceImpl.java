package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 套餐服务接口实现类
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;

    //Freemarker模板对象
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //通过注解方式获取配置文件中的值    @Value("${out_put_path}"):取出配置文件的值 放到outputpath（名字可以任意定义）属性上
    @Value("${out_put_path}")
    private String outputpath;

    /**
     * 新增套餐数据
     */
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        //新增套餐表（单表插入）
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();///检查组id
        //往中间表插入数据(单独抽取一个方法 --- 后续编辑还会用此方法)
        setCheckGroupAndSetmeal(setmealId,checkgroupIds);
        //当新增套餐数据成功后，生成静态页面
        //调用新增套餐后生成静态页面方法
        generateMobileStaticHtml();
    }

    /**
     * 生成套餐列表和 套餐详情页面
     */
    public void generateMobileStaticHtml(){
        //1.获取套餐列表页面数据  （页面需要展示的数据，数据库中非空！！！）
        List<Setmeal> setmealList = this.findAll();
        //2.生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //3.生成套餐详情静态页面
        generateMobileSetmealDetailHtml(setmealList);

    }

    /**
     * 查询所有套餐数据
     */
    public List<Setmeal> findAll(){
        return setmealDao.getSetmeal();//之前已经写好的
    }

    /**
     * 生成套餐列表静态页面
     */
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList){
        //封装套餐列表数据
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("setmealList",setmealList);
        this.generateHtml("mobile_setmeal.ftl",dataMap,"m_setmeal.html");
    }

    /**
     * 生成套餐详情静态页面
     */
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList){
        if(setmealList != null && setmealList.size()>0){
            for (Setmeal setmeal : setmealList) {
                Map<String,Object> dataMap = new HashMap<>();
                //setmealData 模板中需要的数据
                Setmeal setmealData = this.findById(setmeal.getId());
                dataMap.put("setmeal",setmealData);//setmeal跟页面保持一致
                //静态页面文件名称  "setmeal_detail_${setmeal.id}.html" (需要跟页面保持统一)
                this.generateHtml("mobile_setmeal_detail.ftl",dataMap,"setmeal_detail_"+setmeal.getId()+".html");
            }
        }
    }

    /**
     * 定义一个公共的生成静态页面的方法
     * templateName:模板名称
     * dataMap:模板需要的数据
     * htmlPageName：生成的静态页面名称
     */
    public void generateHtml(String templateName,Map<String,Object> dataMap,String htmlPageName){
        Writer out =null;
        try {
            //获取配置类
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //获取模板文件
            Template template = configuration.getTemplate(templateName);
            //定义文件生成路径
            File outFile = new File(outputpath+"\\"+htmlPageName);//C:/working/workspace/javaee-dev87/health_parent/health_mobile_web/src/main/webapp/pages/mobile_setmeal.html
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            //参数1：模板需要的数据  参数2：输出的静态页面
            template.process(dataMap,out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 套餐分页查询
     */
    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        //设置分页参数
        PageHelper.startPage(currentPage, pageSize);//当前页码  每页显示多少条记录
        //需要被分页的语句查询（一定要写到第一行代码后面）
        List<Setmeal> setmealList = setmealDao.selectByCondition(queryString);//分页逻辑交给PageHelper插件搞定了  select * from table where 用户输入的条件 （limit ）
        //封装分页PageInfo对象
        PageInfo<Setmeal> pageInfo = new PageInfo<>(setmealList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
    /**
     * 删除套餐数据
     */
    @Override
    public void deleteById(Integer id) {
        //1.根据套餐id查询 套餐检查组中间表
        int count = setmealDao.findSetmealAndCheckGroupBySetmealId(id);
        //2.存在记录 直接抛出异常
        if(count > 0){
            throw new RuntimeException(MessageConstant.ERROR_CHECKGROUP_SETMEAL);
        }
        //3.直接删除套餐数据
        setmealDao.deleteById(id);

    }
    /**
     * 根据套餐id查询套餐对象
     */
    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    /**
     * 根据套餐id查询所有检查组ids
     */
    @Override
    public List<Integer> findCheckGroupIdsBySetmealId(Integer id) {
        return setmealDao.findCheckGroupIdsBySetmealId(id);
    }

    /**
     * 编辑套餐数据
     */
    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        //1.根据套餐id删除套餐和检查组关系
        setmealDao.deleteReShip(setmeal.getId());
        //2.重新建立套餐和检查组关系
        setCheckGroupAndSetmeal(setmeal.getId(),checkgroupIds);
        //3.更新套餐数据
        setmealDao.edit(setmeal);
    }
    /**
     * 查询所有套餐列表
     */
    @Override
    public List<Setmeal> getSetmeal() {
        return setmealDao.getSetmeal();
    }

    /**
     * 获取套餐名称和套餐对应的预约数量
     * @return
     */
    @Override
    public List<Map<String, Object>> getSetmealCount() {
        return setmealDao.getSetmealCount();
    }

    /***
     * 往检查组和套餐中间表插入数据
     * @param setmealId
     * @param checkgroupIds
     */
    public void setCheckGroupAndSetmeal(Integer setmealId,Integer[] checkgroupIds){
        if(checkgroupIds !=null && checkgroupIds.length>0 ){
            for (Integer checkgroupId : checkgroupIds) {
                //使用map来封装中间表的数据
                Map<String,Integer> map = new HashMap();
                map.put("checkGroupId",checkgroupId);
                map.put("setmealId",setmealId);
                setmealDao.setCheckGroupAndSetmeal(map);
            }
        }
    }
}
