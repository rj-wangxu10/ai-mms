import request from './request';

export function getTools() {
  return request.get('/tool');
}

export function saveTool(data: any) {
  return request.post('/tool', data);
}

export function updateTool(id: number, data: any) {
  return request.put(`/tool/${id}`, data);
}

export function deleteTool(id: number) {
  return request.delete(`/tool/${id}`);
}

export function getModels(toolId: number) {
  return request.get(`/tool/${toolId}/model`);
}

export function saveModel(toolId: number, data: any) {
  return request.post(`/tool/${toolId}/model`, data);
}

export function updateModel(id: number, data: any) {
  return request.put(`/tool/model/${id}`, data);
}

export function deleteModel(id: number) {
  return request.delete(`/tool/model/${id}`);
}
