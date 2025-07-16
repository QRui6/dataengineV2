package com.urban.carbon.base.request;

import java.io.Serializable;

/**
 * 通用查询条件的接口
 *
 * <p>用于定义查询条件的公共接口，所有实现该接口的类应提供具体的查询条件字段及对应的 getter/setter 方法</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>定义具体查询条件类：
 *         <pre>{@code public class UserQueryCondition implements QueryCondition {
 *     private String username;
 *     private Integer status;
 *     // getter and setter
 * }}</pre>
 *     </li>
 *     <li>在 Service 层接收查询条件进行处理：
 *         <pre>{@code public Page<User> queryUsers(UserQueryCondition condition) { ... }}</pre>
 *     </li>
 *     <li>在 Controller 中接收查询参数并传递给业务层：
 *         <pre>{@code @GetMapping("/users") public List<User> getUsers(UserQueryCondition condition) { ... }}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public interface QueryCondition extends Serializable {

}

