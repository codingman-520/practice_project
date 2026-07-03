<template>
  <div v-loading="loading" class="dashboard-container">
    <!-- Stat Cards -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-blue">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-green">
              <el-icon><Share /></el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ stats.totalSkills }}</div>
              <div class="stat-label">技能节点总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-orange">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ stats.totalChats }}</div>
              <div class="stat-label">伴学记录总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-purple">
              <el-icon><Cpu /></el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ stats.totalInterviews }}</div>
              <div class="stat-label">AI 累计调用量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Row -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="14">
        <el-card class="chart-card" header="技能掌握度分布">
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="chart-card" header="最近 7 天 AI 调用趋势">
          <div ref="trendChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Recent Activities Table -->
    <el-card class="table-card" header="最近动态">
      <el-table :data="activities" style="width: 100%" stripe>
        <el-table-column prop="username" label="用户名称" width="180" />
        <el-table-column prop="actionType" label="操作类型" />
        <el-table-column prop="triggerTime" label="触发时间" width="200" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { User, Share, Document, Cpu } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import {
  getDashboardStats,
  getSkillCategoryDistribution,
  getAiCallTrend,
  getRecentActivities
} from '../api/dashboard';

const loading = ref(false);
const stats = ref({
  totalUsers: 0,
  totalSkills: 0,
  totalChats: 0,
  totalInterviews: 0
});
const activities = ref([]);

const pieChartRef = ref(null);
const trendChartRef = ref(null);

let pieChartInstance = null;
let trendChartInstance = null;

onMounted(async () => {
  loading.value = true;
  try {
    const statsRes = await getDashboardStats();
    stats.value = statsRes.data;

    const activitiesRes = await getRecentActivities();
    activities.value = activitiesRes.data;

    const pieRes = await getSkillCategoryDistribution();
    const trendRes = await getAiCallTrend();

    initPieChart(pieRes.data);
    initTrendChart(trendRes.data);
  } catch (error) {
    console.error("加载数据大屏失败", error);
  } finally {
    loading.value = false;
  }
});

const initPieChart = (chartData) => {
  if (pieChartRef.value) {
    pieChartInstance = echarts.init(pieChartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: chartData.map(item => item.name)
      },
      color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'],
      series: [
        {
          name: '技能分类',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: chartData
        }
      ]
    };
    pieChartInstance.setOption(option);
  }
};

const initTrendChart = (chartData) => {
  if (trendChartRef.value) {
    trendChartInstance = echarts.init(trendChartRef.value);
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: chartData.dates
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '调用次数',
          type: 'line',
          smooth: true,
          data: chartData.calls,
          lineStyle: {
            color: '#409EFF',
            width: 3
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 159, 255, 0.4)' },
              { offset: 1, color: 'rgba(64, 159, 255, 0.05)' }
            ])
          },
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    };
    trendChartInstance.setOption(option);
  }
};

onUnmounted(() => {
  if (pieChartInstance) {
    pieChartInstance.dispose();
  }
  if (trendChartInstance) {
    trendChartInstance.dispose();
  }
});
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-row {
  margin-bottom: 5px;
}

.stat-card {
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.icon-blue {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.icon-green {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.icon-orange {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.icon-purple {
  background-color: rgba(155, 89, 182, 0.1);
  color: #9B59B6;
}

.stat-text {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-row {
  margin-bottom: 5px;
}

.chart-card {
  border-radius: 8px;
}

.table-card {
  border-radius: 8px;
}
</style>
