/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.springjdbc.spring.boot.autoconfigure;

import com.jn.langx.util.Platform;
import com.jn.sqlhelper.springjdbc.JdbcTemplate;
import com.jn.sqlhelper.springjdbc.JdbcTemplatePaginationProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;


@Configuration
@ConditionalOnClass({DataSource.class, JdbcTemplate.class})
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableAutoConfiguration(SpringJdbcTemplateProperties.class)
public class JdbcTemplateAutoConfigurationV2 {

    @Configuration
    static class JdbcTemplateConfiguration {

        private final DataSource dataSource;

        private final SpringJdbcTemplateProperties properties;

        JdbcTemplateConfiguration(DataSource dataSource, SpringJdbcTemplateProperties properties) {
            this.dataSource = dataSource;
            this.properties = properties;
        }

        @Bean
        @Primary
        @ConditionalOnMissingBean(JdbcOperations.class)
        public JdbcTemplate jdbcTemplate() {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);

            JdbcTemplateNativeProperties templateConfig = properties.getTemplate();
            jdbcTemplate.setFetchSize(templateConfig.getFetchSize());
            jdbcTemplate.setMaxRows(templateConfig.getMaxRows());
            jdbcTemplate.setQueryTimeout(templateConfig.getQueryTimeout());

            JdbcTemplatePaginationProperties paginationProperties = properties.getPagination();
            jdbcTemplate.setPaginationConfig(paginationProperties);


            return jdbcTemplate;
        }

    }

    @Configuration
    @Import(JdbcTemplateConfiguration.class)
    static class NamedParameterJdbcTemplateConfiguration {

        @Bean
        @Primary
        @ConditionalOnSingleCandidate(JdbcTemplate.class)
        @ConditionalOnMissingBean(NamedParameterJdbcOperations.class)
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

    }

}