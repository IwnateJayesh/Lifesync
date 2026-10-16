/**
 * dashboard.js
 * Lifesyn Dashboard – renders plan, chart, and task cards
 */

const TASK_ICONS = {
  study: '📚',
  exercise: '🏃',
  eat: '🍽️',
  travel: '🚗',
  fuel: '⛽',
  shopping: '🛒',
  rest: '😴',
  'personal work': '💼',
  sleep: '🌙',
};

window.onload = () => {
  const raw = sessionStorage.getItem('lifysyn_schedule');

  if (!raw) {
    window.location.href = 'task-selection.html';
    return;
  }

  const data = JSON.parse(raw);
  renderDashboard(data);
};

function renderDashboard(data) {
  const {
    user,
    plan,
    date,
    occupationType,
    occupiedStart,
    occupiedEnd,
    bufferStart,
    bufferEnd
  } = data;

  document.getElementById('userChipNav').innerHTML = `
    <div class="user-chip">
      <div class="av">${user.name.charAt(0)}</div>
      <span>${user.name.split(' ')[0]}</span>
    </div>
  `;

  const bestTask = plan.find(p => p.final !== 'No suitable slot available') || plan[0];
  const bestIcon = TASK_ICONS[bestTask.task] || '📋';

  const totalTasks = plan.length;
  const assignedTasks = plan.filter(p => p.final !== 'No suitable slot available').length;
  const noSlotTasks = totalTasks - assignedTasks;
  const totalDuration = plan.reduce((s, p) => s + (p.duration || 30), 0);

  const bestOnly = plan.filter(p =>
    p.final !== 'No suitable slot available' && p.final === p.best
  ).length;

  const altUsed = plan.filter(p =>
    p.final !== 'No suitable slot available' && p.final !== p.best
  ).length;

  const avoidCount = noSlotTasks;
  const occHours = computeOccHours(occupiedStart, occupiedEnd);

  const dashboard = document.getElementById('dashboardContent');

  dashboard.innerHTML = `
    <div class="top-bar fade-in">
      <div class="top-bar-left">
        <h1>Your Plan for <span class="gradient-text">${date}</span> ✦</h1>
        <p>${occupationType} · Occupied ${occupiedStart}–${occupiedEnd} · Buffer ${bufferStart}–${bufferEnd}</p>
      </div>
      <div class="top-bar-right">
        <a href="task-selection.html" class="btn btn-outline" style="padding:0.55rem 1.1rem;font-size:0.85rem;">Regenerate</a>
      </div>
    </div>

    <div class="stats-row fade-in fade-in-delay-1">
      <div class="stat-tile">
        <div class="icon">📋</div>
        <div class="val" style="color:var(--accent-teal);">${totalTasks}</div>
        <div class="lbl">Total Tasks</div>
      </div>

      <div class="stat-tile">
        <div class="icon">✅</div>
        <div class="val" style="color:var(--accent-green);">${assignedTasks}</div>
        <div class="lbl">Slots Assigned</div>
      </div>

      <div class="stat-tile">
        <div class="icon">🔴</div>
        <div class="val" style="color:var(--accent-amber);">${avoidCount}</div>
        <div class="lbl">Avoid / No Slot</div>
      </div>

      <div class="stat-tile">
        <div class="icon">⏱️</div>
        <div class="val" style="color:var(--accent-blue);">${Math.round(totalDuration / 60)}h ${totalDuration % 60}m</div>
        <div class="lbl">Total Duration</div>
      </div>
    </div>

    <div class="best-slot-card fade-in fade-in-delay-2">
      <div class="best-slot-content">
        <div class="best-slot-icon-wrap">${bestIcon}</div>

        <div class="best-slot-info">
          <div class="best-slot-task">${bestTask.task.toUpperCase()}</div>
          <div class="best-slot-time">${bestTask.final}</div>
          <div class="best-slot-reason">💡 ${bestTask.reason || 'Recommended based on your selected routine'}</div>
        </div>

        <div class="best-slot-meta">
          <div class="best-slot-duration">${bestTask.duration} min</div>
          <div class="best-slot-dur-label">Duration</div>
          <span class="badge badge-best" style="margin-top:0.5rem;display:inline-block;">Best Slot</span>
        </div>
      </div>
    </div>

    <div class="main-grid fade-in fade-in-delay-3">
      <div>
        <div class="tasks-section-title">📋 Task Recommendations</div>
        <div class="tasks-grid" id="taskCards"></div>
      </div>

      <div>
        <div class="chart-card">
          <div class="chart-card-title">⏰ Time Slot Distribution</div>

          <div class="chart-wrap">
            <canvas id="donutChart" width="200" height="200"></canvas>
          </div>

          <div class="chart-legend">
            <div class="legend-row">
              <div class="legend-dot" style="background:#22d3a0;"></div>
              Best Time Used (${bestOnly} tasks)
            </div>

            <div class="legend-row">
              <div class="legend-dot" style="background:#f59e0b;"></div>
              Alternate Time Used (${altUsed} tasks)
            </div>

            <div class="legend-row">
              <div class="legend-dot" style="background:#f87171;"></div>
              Avoid / No Suitable Slot (${avoidCount} tasks)
            </div>

            <div class="legend-row">
              <div class="legend-dot" style="background:#475569;"></div>
              Occupied + Buffer (~${occHours}h)
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="export-row fade-in">
      <button class="btn btn-outline" onclick="window.print()">🖨️ Print / Save as PDF</button>
    </div>
  `;

  renderTaskCards(plan);
  renderDonutChart(bestOnly, altUsed, avoidCount, occHours);
}

function renderTaskCards(plan) {
  const container = document.getElementById('taskCards');

  if (!container) return;

  container.innerHTML = plan.map((item, idx) => {
    const icon = TASK_ICONS[item.task] || '📋';
    const noSlot = item.final === 'No suitable slot available';
    const isAlt = !noSlot && item.final !== item.best;

    return `
      <div class="task-card ${noSlot ? 'no-slot' : ''}" style="animation: fadeIn 0.5s ease ${idx * 0.08}s both;">
        <div class="task-card-header">
          <div class="task-card-icon">${icon}</div>

          <div>
            <div class="task-card-name">${item.task}</div>
            <div class="task-card-duration">
              ⏱ ${item.duration} min
            </div>
          </div>

          ${isAlt ? `<span class="badge badge-alt" style="margin-left:auto;">Alternate Used</span>` : ''}
          ${!noSlot && !isAlt ? `<span class="badge badge-best" style="margin-left:auto;">Best Used</span>` : ''}
        </div>

        <div class="task-final-banner ${noSlot ? 'no-slot' : ''}">
          <span class="task-final-label">${noSlot ? '⚠️ Recommended Slot' : '✅ Recommended Slot'}</span>
          <span class="task-final-time">${item.final}</span>
        </div>

        <div class="time-rows">
          <div class="time-row-item best-row">
            <span class="tr-label">🟢 Best Time</span>
            <span class="tr-time">${item.best}</span>
          </div>

          <div class="time-row-item alt-row">
            <span class="tr-label">🟡 Alternate Time 1</span>
            <span class="tr-time">${item.alt1}</span>
          </div>

          <div class="time-row-item alt-row">
            <span class="tr-label">🟡 Alternate Time 2</span>
            <span class="tr-time">${item.alt2}</span>
          </div>

          <div class="time-row-item avoid-row">
            <span class="tr-label">🔴 Avoid Time</span>
            <span class="tr-time">${item.avoid}</span>
          </div>
        </div>

        <div class="task-reason">
          💡 ${item.reason || 'Recommended based on offline task preference data'}
        </div>
      </div>
    `;
  }).join('');
}

function renderDonutChart(bestOnly, altUsed, avoidCount, occHours) {
  const canvas = document.getElementById('donutChart');

  if (!canvas) return;

  const ctx = canvas.getContext('2d');

  new Chart(ctx, {
    type: 'doughnut',

    data: {
      labels: [
        'Best Time',
        'Alternate Time',
        'Avoid Time / No Slot',
        'Occupied / Buffer'
      ],

      datasets: [{
        data: [
          Math.max(bestOnly, 0.01),
          Math.max(altUsed, 0.01),
          Math.max(avoidCount, 0.01),
          Math.max(occHours, 0.01)
        ],

        backgroundColor: [
          'rgba(34, 211, 160, 0.85)',
          'rgba(245, 158, 11, 0.85)',
          'rgba(248, 113, 113, 0.85)',
          'rgba(71, 85, 105, 0.7)'
        ],

        borderColor: [
          'rgba(34, 211, 160, 1)',
          'rgba(245, 158, 11, 1)',
          'rgba(248, 113, 113, 1)',
          'rgba(71, 85, 105, 1)'
        ],

        borderWidth: 2,
        hoverOffset: 8
      }]
    },

    options: {
      cutout: '68%',

      plugins: {
        legend: {
          display: false
        },

        tooltip: {
          backgroundColor: 'rgba(10,15,30,0.95)',
          titleColor: '#f0f6ff',
          bodyColor: '#8494b0',
          borderColor: 'rgba(79,142,247,0.3)',
          borderWidth: 1,
          padding: 10,

          callbacks: {
            label: (ctx) => ` ${ctx.label}: ${ctx.formattedValue}`
          }
        }
      },

      animation: {
        animateScale: true,
        duration: 1000,
        easing: 'easeInOutQuart'
      }
    }
  });
}

function computeOccHours(start, end) {
  if (!start || !end) return 9;

  const [sh, sm] = start.split(':').map(Number);
  const [eh, em] = end.split(':').map(Number);

  let startMin = sh * 60 + sm;
  let endMin = eh * 60 + em;

  if (endMin < startMin) {
    endMin += 1440;
  }

  const diff = endMin - startMin + 60;
  return Math.round(diff / 60);
}