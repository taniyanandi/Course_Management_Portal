const state = {
  topics: [],
  courses: [],
  selectedTopicId: null,
  search: "",
  editor: null
};

const elements = {
  apiStatus: document.querySelector("#api-status"),
  topicGrid: document.querySelector("#topic-grid"),
  courseGrid: document.querySelector("#course-grid"),
  filters: document.querySelector("#topic-filters"),
  search: document.querySelector("#course-search"),
  newTopic: document.querySelector("#new-topic-button"),
  newCourse: document.querySelector("#new-course-button"),
  modal: document.querySelector("#editor-modal"),
  form: document.querySelector("#editor-form"),
  fields: document.querySelector("#form-fields"),
  modalTitle: document.querySelector("#modal-title"),
  modalKicker: document.querySelector("#modal-kicker"),
  modalSubmit: document.querySelector("#modal-submit"),
  toastRegion: document.querySelector("#toast-region")
};

const topicIcons = ["✳", "⌘", "◈", "⌬", "✦", "◌"];

async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...options.headers
    }
  });
  if (!response.ok) {
    const detail = await response.text();
    throw new Error(detail || `Request failed (${response.status})`);
  }
  return response.status === 204 ? null : response.json();
}

async function loadDashboard() {
  setConnection("connecting");
  try {
    state.topics = await request("/topics");
    const courseLists = await Promise.all(
      state.topics.map((topic) => request(`/topics/${topic.id}/courses`))
    );
    state.courses = courseLists.flat();
    if (!state.topics.some((topic) => topic.id === state.selectedTopicId)) {
      state.selectedTopicId = null;
    }
    setConnection("connected");
    render();
  } catch (error) {
    setConnection("error");
    showToast(`Couldn't load your learning space: ${error.message}`, true);
    renderEmptyDashboard();
  }
}

function setConnection(status) {
  const labels = { connecting: "Connecting", connected: "API connected", error: "API unavailable" };
  elements.apiStatus.className = `api-status ${status === "connected" ? "connected" : status === "error" ? "error" : ""}`;
  elements.apiStatus.lastElementChild.textContent = labels[status];
}

function render() {
  renderStats();
  renderTopics();
  renderFilters();
  renderCourses();
}

function renderStats() {
  document.querySelector("#stat-topics").textContent = state.topics.length;
  document.querySelector("#stat-courses").textContent = state.courses.length;
  document.querySelector("#stat-next").textContent =
    state.selectedTopicId === null ? "Start anywhere" : selectedTopic()?.course || "Start anywhere";
  document.querySelector("#topic-heading-count").textContent = state.topics.length ? `(${state.topics.length})` : "";
  document.querySelector("#course-heading-count").textContent = state.courses.length ? `(${visibleCourses().length})` : "";
  document.querySelector("#sidebar-topic-count").textContent = state.topics.length;
}

function renderTopics() {
  if (!state.topics.length) {
    elements.topicGrid.innerHTML = emptyState(
      "Your first topic starts here",
      "Create a topic for something you want to learn, explore, or teach.",
      "Create a topic",
      "new-topic"
    );
    return;
  }

  elements.topicGrid.innerHTML = state.topics.map((topic, index) => {
    const count = state.courses.filter((course) => course.topic?.id === topic.id).length;
    const selected = topic.id === state.selectedTopicId;
    return `
      <article class="topic-card ${selected ? "selected" : ""}" data-topic-card="${topic.id}" tabindex="0" role="button" aria-pressed="${selected}" aria-label="Show ${escapeHtml(topic.course)} courses">
        <div class="topic-top">
          <span class="topic-icon tone-${index % topicIcons.length}" aria-hidden="true">${topicIcons[index % topicIcons.length]}</span>
          <button class="topic-more" type="button" data-edit-topic="${topic.id}" aria-label="Edit ${escapeHtml(topic.course)} topic">···</button>
        </div>
        <span class="topic-name">${escapeHtml(topic.course)}</span>
        <span class="topic-meta"><span>${count} ${count === 1 ? "course" : "courses"}</span><span class="topic-arrow" aria-hidden="true">↗</span></span>
      </article>`;
  }).join("");
}

function renderFilters() {
  const allActive = state.selectedTopicId === null;
  elements.filters.innerHTML = [
    `<button class="filter-chip ${allActive ? "active" : ""}" type="button" data-filter="all" aria-pressed="${allActive}">All courses</button>`,
    ...state.topics.map((topic) => {
      const active = topic.id === state.selectedTopicId;
      return `<button class="filter-chip ${active ? "active" : ""}" type="button" data-filter="${topic.id}" aria-pressed="${active}">${escapeHtml(topic.course)}</button>`;
    })
  ].join("");
  elements.newCourse.disabled = state.topics.length === 0;
  const topic = selectedTopic();
  document.querySelector("#courses-title").innerHTML = topic
    ? `${escapeHtml(topic.course)} courses <span id="course-heading-count" class="heading-count">${visibleCourses().length ? `(${visibleCourses().length})` : ""}</span>`
    : `Courses <span id="course-heading-count" class="heading-count">${state.courses.length ? `(${visibleCourses().length})` : ""}</span>`;
}

function renderCourses() {
  const courses = visibleCourses();
  if (!courses.length) {
    const hasSearch = state.search.trim().length > 0;
    const hasTopic = state.topics.length > 0;
    const title = hasSearch ? "No courses match that search" : hasTopic ? "A fresh page to fill" : "Start with a topic";
    const copy = hasSearch
      ? "Try another title or clear your search."
      : hasTopic
        ? "Add your first course and turn curiosity into a learning path."
        : "Create a topic first. Your courses will have a home here.";
    elements.courseGrid.innerHTML = emptyState(title, copy, hasTopic && !hasSearch ? "Add your first course" : "", "new-course");
    return;
  }
  elements.courseGrid.innerHTML = courses.map((course) => `
    <article class="course-card">
      <div class="course-card-top">
        <span class="course-category"><span class="course-category-dot"></span>${escapeHtml(course.topic?.course || "Your course")}</span>
        <span class="course-actions">
          <button class="tiny-action" type="button" data-edit-course="${course.id}" aria-label="Edit ${escapeHtml(course.name)}">✎</button>
          <button class="tiny-action" type="button" data-delete-course="${course.id}" aria-label="Delete ${escapeHtml(course.name)}">×</button>
        </span>
      </div>
      <h3 class="course-name">${escapeHtml(course.name)}</h3>
      <p class="course-description">${escapeHtml(course.description)}</p>
    </article>`).join("");
}

function renderEmptyDashboard() {
  state.topics = [];
  state.courses = [];
  render();
  elements.topicGrid.innerHTML = emptyState("Your learning space is taking a breather", "Check that the application is running, then refresh to reconnect.", "Try again", "refresh");
}

function emptyState(title, copy, action, actionType) {
  return `<div class="empty-state"><div><span class="empty-symbol" aria-hidden="true">✳</span><p class="empty-title">${escapeHtml(title)}</p><p class="empty-copy">${escapeHtml(copy)}</p>${action ? `<button class="button button-secondary empty-action" type="button" data-empty-action="${actionType}">${escapeHtml(action)}</button>` : ""}</div></div>`;
}

function visibleCourses() {
  const query = state.search.trim().toLowerCase();
  return state.courses.filter((course) => {
    const matchesTopic = state.selectedTopicId === null || course.topic?.id === state.selectedTopicId;
    const matchesSearch = !query || `${course.name} ${course.description} ${course.topic?.course || ""}`.toLowerCase().includes(query);
    return matchesTopic && matchesSearch;
  });
}

function selectedTopic() {
  return state.topics.find((topic) => topic.id === state.selectedTopicId);
}

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (character) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[character]);
}

function showToast(message, isError = false) {
  const toast = document.createElement("div");
  toast.className = `toast${isError ? " error" : ""}`;
  toast.textContent = message;
  elements.toastRegion.append(toast);
  window.setTimeout(() => toast.remove(), 4200);
}

function openTopicEditor(topic = null) {
  state.editor = topic
    ? { type: "topic", id: topic.id }
    : { type: "topic" };
  elements.modalKicker.textContent = topic ? "REFINE YOUR LEARNING SPACE" : "GROW YOUR LEARNING SPACE";
  elements.modalTitle.textContent = topic ? "Edit topic" : "New topic";
  elements.modalSubmit.textContent = topic ? "Save changes" : "Create topic";
  elements.fields.innerHTML = `
    <label class="form-field">Topic name
      <input name="course" maxlength="120" required placeholder="e.g. Product design" value="${escapeHtml(topic?.course || "")}" autocomplete="off">
    </label>
    ${topic ? `<button class="button button-secondary" type="button" data-delete-topic="${topic.id}">Delete this topic</button>` : ""}`;
  elements.modal.showModal();
  elements.fields.querySelector("input").focus();
}

function openCourseEditor(course = null) {
  if (!state.topics.length) {
    showToast("Create a topic before adding a course.");
    return;
  }
  const defaultTopic = selectedTopic() || state.topics[0];
  state.editor = { type: "course", id: course?.id ?? null, topicId: course?.topic?.id ?? defaultTopic.id };
  elements.modalKicker.textContent = course ? "KEEP YOUR LEARNING FRESH" : "ADD TO YOUR LEARNING PATH";
  elements.modalTitle.textContent = course ? "Edit course" : "New course";
  elements.modalSubmit.textContent = course ? "Save changes" : "Add course";
  elements.fields.innerHTML = `
    <label class="form-field">Course name
      <input name="name" maxlength="160" required placeholder="e.g. The craft of clear writing" value="${escapeHtml(course?.name || "")}" autocomplete="off">
    </label>
    <label class="form-field">Topic
      <select name="topicId" required>${state.topics.map((topic) => `<option value="${topic.id}" ${topic.id === state.editor.topicId ? "selected" : ""}>${escapeHtml(topic.course)}</option>`).join("")}</select>
    </label>
    <label class="form-field">What will you learn?
      <textarea name="description" maxlength="1000" required placeholder="A short note about what makes this course worth exploring.">${escapeHtml(course?.description || "")}</textarea>
    </label>`;
  elements.modal.showModal();
  elements.fields.querySelector("input").focus();
}

async function submitEditor(event) {
  event.preventDefault();
  if (!state.editor) return;
  const formData = new FormData(elements.form);
  const editing = state.editor.id !== undefined && state.editor.id !== null;
  try {
    if (state.editor.type === "topic") {
      const topic = { course: String(formData.get("course")).trim() };
      await request(editing ? `/topics/${state.editor.id}` : "/topics", {
        method: editing ? "PUT" : "POST",
        body: JSON.stringify(topic)
      });
      showToast(editing ? "Topic updated." : "Topic created.");
    } else {
      const topicId = Number(formData.get("topicId"));
      const course = {
        name: String(formData.get("name")).trim(),
        description: String(formData.get("description")).trim()
      };
      await request(editing ? `/topics/${topicId}/courses/${state.editor.id}` : `/topics/${topicId}/courses`, {
        method: editing ? "PUT" : "POST",
        body: JSON.stringify(course)
      });
      showToast(editing ? "Course updated." : "Course added.");
    }
    elements.modal.close();
    await loadDashboard();
  } catch (error) {
    showToast(`Couldn't save changes: ${error.message}`, true);
  }
}

async function deleteTopic(id) {
  const topic = state.topics.find((item) => item.id === id);
  if (!topic || !window.confirm(`Delete "${topic.course}" and all of its courses? This can't be undone.`)) return;
  try {
    await request(`/topics/${id}`, { method: "DELETE" });
    if (state.selectedTopicId === id) state.selectedTopicId = null;
    elements.modal.close();
    showToast("Topic and its courses deleted.");
    await loadDashboard();
  } catch (error) {
    showToast(`Couldn't delete topic: ${error.message}`, true);
  }
}

async function deleteCourse(id) {
  const course = state.courses.find((item) => item.id === id);
  if (!course || !window.confirm(`Delete "${course.name}"? This can't be undone.`)) return;
  try {
    await request(`/topics/${course.topic.id}/courses/${id}`, { method: "DELETE" });
    showToast("Course deleted.");
    await loadDashboard();
  } catch (error) {
    showToast(`Couldn't delete course: ${error.message}`, true);
  }
}

document.querySelector("#new-topic-button").addEventListener("click", () => openTopicEditor());
elements.newCourse.addEventListener("click", () => openCourseEditor());
document.querySelector("#refresh-button").addEventListener("click", loadDashboard);
document.querySelector("#view-all-button").addEventListener("click", () => {
  state.selectedTopicId = null;
  render();
  document.querySelector("#your-learning").scrollIntoView({ behavior: "smooth", block: "start" });
});
document.querySelector("#sidebar-topics").addEventListener("click", () => {
  document.querySelector("#your-learning").scrollIntoView({ behavior: "smooth", block: "start" });
});
elements.search.addEventListener("input", (event) => {
  state.search = event.target.value;
  renderStats();
  renderCourses();
});
elements.form.addEventListener("submit", submitEditor);
document.querySelector("#modal-close").addEventListener("click", () => elements.modal.close());
document.querySelector("#modal-cancel").addEventListener("click", () => elements.modal.close());

document.addEventListener("click", (event) => {
  const target = event.target;
  const topicCard = target.closest("[data-topic-card]");
  if (topicCard && !target.closest("[data-edit-topic]")) {
    const id = Number(topicCard.dataset.topicCard);
    state.selectedTopicId = state.selectedTopicId === id ? null : id;
    render();
    return;
  }
  const editTopic = target.closest("[data-edit-topic]");
  if (editTopic) {
    event.stopPropagation();
    openTopicEditor(state.topics.find((topic) => topic.id === Number(editTopic.dataset.editTopic)));
    return;
  }
  const editCourse = target.closest("[data-edit-course]");
  if (editCourse) {
    openCourseEditor(state.courses.find((course) => course.id === Number(editCourse.dataset.editCourse)));
    return;
  }
  const removeCourse = target.closest("[data-delete-course]");
  if (removeCourse) {
    deleteCourse(Number(removeCourse.dataset.deleteCourse));
    return;
  }
  const removeTopic = target.closest("[data-delete-topic]");
  if (removeTopic) {
    deleteTopic(Number(removeTopic.dataset.deleteTopic));
    return;
  }
  const filter = target.closest("[data-filter]");
  if (filter) {
    state.selectedTopicId = filter.dataset.filter === "all" ? null : Number(filter.dataset.filter);
    render();
    return;
  }
  const emptyAction = target.closest("[data-empty-action]");
  if (emptyAction) {
    if (emptyAction.dataset.emptyAction === "new-topic") openTopicEditor();
    if (emptyAction.dataset.emptyAction === "new-course") openCourseEditor();
    if (emptyAction.dataset.emptyAction === "refresh") loadDashboard();
  }
});

document.addEventListener("keydown", (event) => {
  const topicCard = event.target.closest?.("[data-topic-card]");
  if (topicCard && (event.key === "Enter" || event.key === " ")) {
    event.preventDefault();
    topicCard.click();
  }
});

loadDashboard();
